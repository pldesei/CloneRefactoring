        if (ncrit != null) {
            for (Iterator<String> iter = ncrit.iterator(); iter.hasNext();) {
                String ncritOid = iter.next();
                byte[] value = cert.getExtensionValue(ncritOid);
                // TODO: wired. J9 returns null for an OID given in getNonCriticalExtensionOIDs()
                if (value != null) {
                    IRubyObject rValue = ASN1.decode(ossl.getConstant("ASN1"), runtime.newString(new ByteList(value, false))).callMethod(context, "value");
                    X509Extensions.Extension ext = (X509Extensions.Extension) x509.getConstant("Extension").callMethod(context, "new",
                            new IRubyObject[] { runtime.newString(ncritOid), rValue, runtime.getFalse() });
                    add_extension(ext);
                }
            }
        }
