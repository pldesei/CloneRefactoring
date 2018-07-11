        if (crit != null) {
            for (Iterator<String> iter = crit.iterator(); iter.hasNext();) {
                String critOid = iter.next();
                byte[] value = cert.getExtensionValue(critOid);
                IRubyObject rValue = ASN1.decode(ossl.getConstant("ASN1"), runtime.newString(new ByteList(value, false))).callMethod(context, "value");
                X509Extensions.Extension ext = (X509Extensions.Extension) x509.getConstant("Extension").callMethod(context, "new",
                        new IRubyObject[] { runtime.newString(critOid), rValue, runtime.getTrue() });
                add_extension(ext);
            }
        }
