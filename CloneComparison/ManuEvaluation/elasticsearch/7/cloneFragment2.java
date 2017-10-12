This clone fragment is located in File: 
core/src/test/java/org/elasticsearch/cluster/routing/allocation/AddIncrementallyTests.java
The line range of this clone fragment is: 156-216
The content of this clone fragment is as follows:
    public void testMinimalRelocationsNoLimit() {
        Settings.Builder settings = Settings.builder();
        settings.put(ClusterRebalanceAllocationDecider.CLUSTER_ROUTING_ALLOCATION_ALLOW_REBALANCE_SETTING.getKey(), ClusterRebalanceAllocationDecider.ClusterRebalanceType.ALWAYS.toString())
                .put("cluster.routing.allocation.node_concurrent_recoveries", 100)
                .put("cluster.routing.allocation.node_initial_primaries_recoveries", 100);
        AllocationService service = createAllocationService(settings.build());

        ClusterState clusterState = initCluster(service, 1, 3, 3, 1);
        assertThat(clusterState.getRoutingNodes().node("node0").shardsWithState(STARTED).size(), equalTo(9));
        assertThat(clusterState.getRoutingNodes().unassigned().size(), equalTo(9));
        int nodeOffset = 1;
        clusterState = addNodes(clusterState, service, 1, nodeOffset++);
        assertThat(clusterState.getRoutingNodes().node("node0").shardsWithState(STARTED).size(), equalTo(9));
        assertThat(clusterState.getRoutingNodes().node("node1").shardsWithState(STARTED).size(), equalTo(9));
        assertThat(clusterState.getRoutingNodes().unassigned().size(), equalTo(0));
        assertNumIndexShardsPerNode(clusterState, equalTo(3));

        logger.info("now, start one more node, check that rebalancing will happen because we set it to always");
        DiscoveryNodes.Builder nodes = DiscoveryNodes.builder(clusterState.nodes());
        nodes.add(newNode("node2"));
        clusterState = ClusterState.builder(clusterState).nodes(nodes.build()).build();

        clusterState = service.reroute(clusterState, "reroute");
        RoutingNodes routingNodes = clusterState.getRoutingNodes();

        assertThat(clusterState.getRoutingNodes().node("node2").shardsWithState(INITIALIZING).size(), equalTo(2));
        assertThat(clusterState.getRoutingNodes().node("node0").shardsWithState(INITIALIZING).size(), equalTo(0));
        assertThat(clusterState.getRoutingNodes().node("node1").shardsWithState(INITIALIZING).size(), equalTo(0));

        ClusterState newState = service.applyStartedShards(clusterState, routingNodes.shardsWithState(INITIALIZING));
        assertThat(newState, not(equalTo(clusterState)));
        clusterState = newState;
        routingNodes = clusterState.getRoutingNodes();
        assertThat(clusterState.getRoutingNodes().node("node2").shardsWithState(STARTED).size(), equalTo(2));
        assertThat(clusterState.getRoutingNodes().node("node2").shardsWithState(INITIALIZING).size(), equalTo(2));
        assertThat(clusterState.getRoutingNodes().node("node0").shardsWithState(INITIALIZING).size(), equalTo(0));
        assertThat(clusterState.getRoutingNodes().node("node1").shardsWithState(INITIALIZING).size(), equalTo(0));

        newState = service.applyStartedShards(clusterState, routingNodes.shardsWithState(INITIALIZING));
        assertThat(newState, not(equalTo(clusterState)));
        clusterState = newState;
        routingNodes = clusterState.getRoutingNodes();
        assertThat(clusterState.getRoutingNodes().node("node2").shardsWithState(STARTED).size(), equalTo(4));
        assertThat(clusterState.getRoutingNodes().node("node2").shardsWithState(INITIALIZING).size(), equalTo(2));
        assertThat(clusterState.getRoutingNodes().node("node0").shardsWithState(INITIALIZING).size(), equalTo(0));
        assertThat(clusterState.getRoutingNodes().node("node1").shardsWithState(INITIALIZING).size(), equalTo(0));

        newState = service.applyStartedShards(clusterState, routingNodes.shardsWithState(INITIALIZING));
        assertThat(newState, not(equalTo(clusterState)));
        clusterState = newState;
        routingNodes = clusterState.getRoutingNodes();
        assertThat(clusterState.getRoutingNodes().node("node2").shardsWithState(STARTED).size(), equalTo(6));
        assertThat(clusterState.getRoutingNodes().node("node2").shardsWithState(INITIALIZING).size(), equalTo(0));
        assertThat(clusterState.getRoutingNodes().node("node0").shardsWithState(INITIALIZING).size(), equalTo(0));
        assertThat(clusterState.getRoutingNodes().node("node1").shardsWithState(INITIALIZING).size(), equalTo(0));

        newState = service.applyStartedShards(clusterState, routingNodes.shardsWithState(INITIALIZING));
        assertThat(newState, equalTo(clusterState));
        assertNumIndexShardsPerNode(clusterState, equalTo(2));
        logger.debug("ClusterState: {}", clusterState.getRoutingNodes());
    }