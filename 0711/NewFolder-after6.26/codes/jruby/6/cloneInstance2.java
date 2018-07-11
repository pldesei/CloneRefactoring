            } else if (Character.isDigit(lNext)) {
                int lEndIndex = i;
                for (; lEndIndex < lFmtLength; lEndIndex++)
                    if (!Character.isDigit(lFmt[lEndIndex]))
                        break;
                lLength = Integer.parseInt(new String(lFmt, i, lEndIndex - i));
                //an exception may occur here if an int can't hold this but ...
                i = lEndIndex;
                lNext = i < lFmtLength ? lFmt[i] : '\0';
            } //no else, the original value of length is correct
