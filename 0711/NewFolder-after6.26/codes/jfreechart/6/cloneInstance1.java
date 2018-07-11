            if (state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    if (getToolTipGenerator(row, column) != null) {
                        tip = getToolTipGenerator(row, column).generateToolTip(
                                dataset, row, column);
                    }
                    String url = null;
                    if (getItemURLGenerator(row, column) != null) {
                        url = getItemURLGenerator(row, column).generateURL(
                                dataset, row, column);
                    }
                    CategoryItemEntity entity = new CategoryItemEntity(
                            bar, tip, url, dataset, dataset.getRowKey(row),
                            dataset.getColumnKey(column));
                    entities.add(entity);
                }
            }
