           int compression) throws IOException {

        if (file == null) {
            throw new IllegalArgumentException("Null 'file' argument.");
        }
        if (chart == null) {
            throw new IllegalArgumentException("Null 'chart' argument.");
        }

        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            writeChartAsPNG(out, chart, width, height, info, encodeAlpha,
                    compression);
        }
        finally {
            out.close();
        }

    }
