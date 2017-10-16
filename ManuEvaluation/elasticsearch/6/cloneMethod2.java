This clone method is located in File: 
core/src/test/java/org/elasticsearch/gateway/RecoverAfterNodesIT.java
The line range of this clone method is: 130-176
The content of this clone method is as follows:
    public void testRecoverAfterDataNodes() throws Exception {
        logger.info("--> start master_node (1)");
        Client master1 = startNode(Settings.builder()
            .put("gateway.recover_after_data_nodes", 2).put(Node.NODE_DATA_SETTING.getKey(), false)
            .put(Node.NODE_MASTER_SETTING.getKey(), true), 1);
        assertThat(master1.admin().cluster().prepareState().setLocal(true).execute().actionGet()
                .getState().blocks().global(ClusterBlockLevel.METADATA_WRITE),
                hasItem(GatewayService.STATE_NOT_RECOVERED_BLOCK));

        logger.info("--> start data_node (1)");
        Client data1 = startNode(Settings.builder()
            .put("gateway.recover_after_data_nodes", 2)
            .put(Node.NODE_DATA_SETTING.getKey(), true).put(Node.NODE_MASTER_SETTING.getKey(), false), 1);
        assertThat(master1.admin().cluster().prepareState().setLocal(true).execute().actionGet()
                .getState().blocks().global(ClusterBlockLevel.METADATA_WRITE),
                hasItem(GatewayService.STATE_NOT_RECOVERED_BLOCK));
        assertThat(data1.admin().cluster().prepareState().setLocal(true).execute().actionGet()
                .getState().blocks().global(ClusterBlockLevel.METADATA_WRITE),
                hasItem(GatewayService.STATE_NOT_RECOVERED_BLOCK));

        logger.info("--> start master_node (2)");
        Client master2 = startNode(Settings.builder()
            .put("gateway.recover_after_data_nodes", 2).put(Node.NODE_DATA_SETTING.getKey(), false)
            .put(Node.NODE_MASTER_SETTING.getKey(), true), 1);
        assertThat(master2.admin().cluster().prepareState().setLocal(true).execute().actionGet()
                .getState().blocks().global(ClusterBlockLevel.METADATA_WRITE),
                hasItem(GatewayService.STATE_NOT_RECOVERED_BLOCK));
        assertThat(data1.admin().cluster().prepareState().setLocal(true).execute().actionGet()
                .getState().blocks().global(ClusterBlockLevel.METADATA_WRITE),
                hasItem(GatewayService.STATE_NOT_RECOVERED_BLOCK));
        assertThat(master2.admin().cluster().prepareState().setLocal(true).execute().actionGet()
                .getState().blocks().global(ClusterBlockLevel.METADATA_WRITE),
                hasItem(GatewayService.STATE_NOT_RECOVERED_BLOCK));

        logger.info("--> start data_node (2)");
        Client data2 = startNode(Settings.builder()
            .put("gateway.recover_after_data_nodes", 2)
            .put(Node.NODE_DATA_SETTING.getKey(), true)
            .put(Node.NODE_MASTER_SETTING.getKey(), false), 1);
        assertThat(waitForNoBlocksOnNode(BLOCK_WAIT_TIMEOUT, master1).isEmpty(), equalTo(true));
        assertThat(waitForNoBlocksOnNode(BLOCK_WAIT_TIMEOUT, master2).isEmpty(), equalTo(true));
        assertThat(waitForNoBlocksOnNode(BLOCK_WAIT_TIMEOUT, data1).isEmpty(), equalTo(true));
        assertThat(waitForNoBlocksOnNode(BLOCK_WAIT_TIMEOUT, data2).isEmpty(), equalTo(true));
    }
