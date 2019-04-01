package org.conquernos.cinnamon.manager.control.shover;


import org.conquernos.cinnamon.exception.shover.ShoverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ShoverControl {

    private static final Logger logger = LoggerFactory.getLogger(ShoverControl.class);

    private final Map<String, Shover> shovers = new HashMap<>();


    public ShoverControl() {
    }

    public void registerShover(String id, Shover shover) throws ShoverException {
        shovers.put(id, shover);
    }

    public Shover getShover(String id) {
        return shovers.get(id);
    }

    public Collection<Shover> getShovers() {
        return shovers.values();
    }

}
