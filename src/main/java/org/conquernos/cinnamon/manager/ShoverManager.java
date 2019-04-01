package org.conquernos.cinnamon.manager;


import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.PatternsCS;
import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.control.shover.ShoverControl;
import org.conquernos.cinnamon.exception.shover.ShoverException;
import org.conquernos.cinnamon.message.Message;
import org.conquernos.cinnamon.message.broker.BrokerMessage;
import org.conquernos.cinnamon.message.schema.SchemaMessage;
import org.conquernos.cinnamon.message.shover.ShoverMessage;

import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;


public class ShoverManager extends Manager {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final ActorRef master;

    private final ShoverControl shoverControl;

    private final long messageTimeout;


    public ShoverManager(ActorRef master, MasterConfig config) throws ShoverException {
        this.master = master;
        this.messageTimeout = config.getMessageTimeout();

        shoverControl = new ShoverControl();

        logger.debug("ShoverManager({})", getSelf().path());
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        logger.debug("ShoverManager({}) onReceive - {}", getSelf(), message);

        if (message instanceof ShoverMessage.ShoverRegistrationRequest) {
            registerShover((ShoverMessage.ShoverRegistrationRequest) message, getSender());
        } else if (message instanceof ShoverMessage.ShoversInquiryRequest) {
            inquiryShovers((ShoverMessage.ShoversInquiryRequest) message, getSender());
        } else if (message instanceof ShoverMessage.ShoverInquiryRequest) {
            inquiryShover((ShoverMessage.ShoverInquiryRequest) message, getSender());
        }

    }

    private void registerShover(ShoverMessage.ShoverRegistrationRequest request, ActorRef requester) {
        try {
            shoverControl.registerShover(request.getShover().getId(), request.getShover());

            CompletionStage<BrokerMessage.BrokerUrlResponse> brokerUrls = PatternsCS.ask(master, new BrokerMessage.BrokerUrlRequest(), messageTimeout)
                .thenApply(BrokerMessage.BrokerUrlResponse.class::cast);

            CompletionStage<SchemaMessage.SchemaRegistryUrlResponse> schemaRegistryUrls = PatternsCS.ask(master, new SchemaMessage.SchemaRegistryUrlRequest(), messageTimeout)
                .thenApply(SchemaMessage.SchemaRegistryUrlResponse.class::cast);

            brokerUrls.thenAcceptBoth(schemaRegistryUrls, (brokerUrlResponse, schemaRegistryUrlResponse) ->
                requester.tell(new ShoverMessage.ShoverRegistrationResponse(Message.Result.SUCCESS, brokerUrlResponse.getUrls(), schemaRegistryUrlResponse.getUrls()), master)
            );
        } catch (ShoverException e) {
            requester.tell(new ShoverMessage.ShoverRegistrationResponse(Message.Result.FAIL, "fail to register the shover : " + request.getShover()), master);
            e.printStackTrace();
        }
    }

    private void inquiryShovers(ShoverMessage.ShoversInquiryRequest request, ActorRef requester) {
        try {
            requester.tell(new ShoverMessage.ShoversInquiryResponse(Message.Result.SUCCESS, shoverControl.getShovers()), master);
        } catch (Exception e) {
            requester.tell(new ShoverMessage.ShoversInquiryResponse(Message.Result.FAIL, "fail to inquiry shovers"), master);
            e.printStackTrace();
        }
    }

    private void inquiryShover(ShoverMessage.ShoverInquiryRequest request, ActorRef requester) {
        try {
            requester.tell(new ShoverMessage.ShoverInquiryResponse(Message.Result.SUCCESS, shoverControl.getShover(request.getId())), master);
        } catch (Exception e) {
            requester.tell(new ShoverMessage.ShoverInquiryResponse(Message.Result.FAIL, "fail to inquiry shover : " + request.getId()), master);
            e.printStackTrace();
        }
    }

}
