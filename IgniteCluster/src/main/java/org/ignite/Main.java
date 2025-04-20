package org.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.Affinity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main  {

    public static void main(String[] args) {


        Ignite ignite = null;

        ignite = Ignition.start("ignite-config2.xml");

        while(true){

        }

    }
}