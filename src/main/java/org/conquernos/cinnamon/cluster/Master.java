package org.conquernos.cinnamon.cluster;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import org.conquernos.cinnamon.cluster.api.rest.HttpService;
import org.conquernos.cinnamon.config.HttpServiceConfig;
import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.BrokerManager;
import org.conquernos.cinnamon.manager.SchemaManager;
import org.conquernos.cinnamon.manager.IngesterManager;
import org.conquernos.cinnamon.manager.ShoverManager;
import org.conquernos.cinnamon.manager.control.broker.KafkaAdminClient;
import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;
import org.conquernos.cinnamon.utils.config.ConfigLoader;
import kafka.admin.AdminClient;
import org.conquernos.cinnamon.message.broker.BrokerMessage;
import org.conquernos.cinnamon.message.ingester.IngesterMessage;
import org.conquernos.cinnamon.message.schema.SchemaMessage;
import org.conquernos.cinnamon.message.shover.ShoverMessage;


public class Master extends UntypedActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private static final String SHOVER_MANAGER_ROUTER_NAME = "shoverManagerRouter";
    private static final String SCHEMA_MANAGER_ROUTER_NAME = "schemaManagerRouter";
    private static final String BROKER_MANAGER_ROUTER_NAME = "brokerManagerRouter";
    private static final String INGESTER_MANAGER_ROUTER_NAME = "ingesterManagerRouter";

    private final MasterConfig config;

    private HttpService httpService;

    private Zookeeper zookeeper;
    private AdminClient kafkaAdmin;

    private ActorRef shoverManagerRouter;
    private ActorRef schemaManagerRouter;
    private ActorRef brokerManagerRouter;
    private ActorRef ingesterManagerRouter;


    public Master(MasterConfig config) {
        this.config = config;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        HttpServiceConfig httpServiceConfig = (HttpServiceConfig) ConfigLoader.build(HttpServiceConfig.class, config.getConfig());
        httpService = new HttpService(ActorSystem.create(), getSelf(), httpServiceConfig);
        httpService.open();

        zookeeper = new Zookeeper(config.getKafkaZkServers(), config.getKafkaZkTimeout(), false);

        kafkaAdmin = KafkaAdminClient.create(config);

        createSchemaManagers(zookeeper);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        httpService.close();
        zookeeper.close();
    }

    @Override
    public void onReceive(Object message) throws Throwable {

        // Schema-registry
        if (message instanceof ShoverMessage.ShoverRequest) {
            shoverManagerRouter.forward(message, getContext());

        } else if (message instanceof SchemaMessage.SchemaRequest) {
            schemaManagerRouter.forward(message, getContext());

        // Broker-registry
        } else if (message instanceof BrokerMessage.BrokerRequest) {
            brokerManagerRouter.forward(message, getContext());

        // Ingester-registry
        } else if (message instanceof IngesterMessage.IngesterRequest) {
            ingesterManagerRouter.forward(message, getContext());

        } else if (message instanceof Terminated) {
            logger.debug("TERMINATED : {}", ((Terminated) message).getActor().path());
        }

    }

    private void createSchemaManagers(Zookeeper zookeeper) {
//        SupervisorStrategy strategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES),
//                Collections.singletonList(Exception.class));

//        schemaManagerRouter = getContext().actorOf(
//            FromConfig.getInstance().props(Props.create(SchemaManager.class, getSelf(), config))
//            , SCHEMA_MANAGER_ROUTER_NAME);

        shoverManagerRouter = getContext().actorOf(Props.create(ShoverManager.class, getSelf(), config)
            .withRouter(new FromConfig()), SHOVER_MANAGER_ROUTER_NAME);

        schemaManagerRouter = getContext().actorOf(Props.create(SchemaManager.class, getSelf(), config)
            .withRouter(new FromConfig()), SCHEMA_MANAGER_ROUTER_NAME);

        brokerManagerRouter = getContext().actorOf(Props.create(BrokerManager.class, getSelf(), config, zookeeper)
            .withRouter(new FromConfig()), BROKER_MANAGER_ROUTER_NAME);

        ingesterManagerRouter = getContext().actorOf(Props.create(IngesterManager.class, getSelf(), config)
            .withRouter(new FromConfig()), INGESTER_MANAGER_ROUTER_NAME);
    }

}
