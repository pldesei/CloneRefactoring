This clone method is located in File: 
modules/lang-painless/src/main/java/org/elasticsearch/painless/node/EBinary.java
The line range of this clone method is: 410-457
The content of this clone method is as follows:
    private void analyzeRSH(Locals variables) {
        left.analyze(variables);
        right.analyze(variables);

        Type lhspromote = AnalyzerCaster.promoteNumeric(left.actual, false);
        Type rhspromote = AnalyzerCaster.promoteNumeric(right.actual, false);

        if (lhspromote == null || rhspromote == null) {
            throw createError(new ClassCastException("Cannot apply right shift [>>] to types " +
                "[" + left.actual.name + "] and [" + right.actual.name + "]."));
        }

        actual = promote = lhspromote;
        shiftDistance = rhspromote;

        if (lhspromote.sort == Sort.DEF || rhspromote.sort == Sort.DEF) {
            left.expected = left.actual;
            right.expected = right.actual;

            if (expected != null) {
                actual = expected;
            }
        } else {
            left.expected = lhspromote;

            if (rhspromote.sort == Sort.LONG) {
                right.expected = Definition.INT_TYPE;
                right.explicit = true;
            } else {
                right.expected = rhspromote;
            }
        }

        left = left.cast(variables);
        right = right.cast(variables);

        if (left.constant != null && right.constant != null) {
            Sort sort = lhspromote.sort;

            if (sort == Sort.INT) {
                constant = (int)left.constant >> (int)right.constant;
            } else if (sort == Sort.LONG) {
                constant = (long)left.constant >> (int)right.constant;
            } else {
                throw createError(new IllegalStateException("Illegal tree structure."));
            }
        }
    }
