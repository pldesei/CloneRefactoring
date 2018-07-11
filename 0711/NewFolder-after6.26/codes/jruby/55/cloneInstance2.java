            } else if (type == X509Utils.X509_FILETYPE_ASN1) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                x = cf.generateCRL(in);
                if (x == null) {
                    X509Error.addError(13);
                    return ret;
                }
                int i = store.addCRL(x);
                if (i == 0) {
                    return ret;
                }
                ret = i;
            } else {
