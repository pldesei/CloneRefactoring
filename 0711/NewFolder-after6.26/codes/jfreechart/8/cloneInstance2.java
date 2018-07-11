        if (state.getInfo() != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                String tip = null;
                CategoryToolTipGenerator tipster
                    = getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(data, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(
                        data, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(bar, tip,
                        url, data, data.getRowKey(row),
                        data.getColumnKey(column));
                entities.add(entity);
            }
        }
