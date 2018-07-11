                    if (md.getDeclaringType().getDeclaringType() != null) {
                        // inner class, use $ to delimit
                        if (md.getDeclaringType().getDeclaringType().getDeclaringType() != null) {
                            qualifiedName = md.getDeclaringType().getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getDeclaringType().getSimpleName() + "$" + md.getDeclaringType().getSimpleName();
                        } else {
                            qualifiedName = md.getDeclaringType().getDeclaringType().getQualifiedName() + "$" + md.getDeclaringType().getSimpleName();
                        }
                    } else {
