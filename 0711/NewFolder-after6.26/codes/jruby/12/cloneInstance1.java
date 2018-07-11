	public CharSequence scanUntil(Regex pattern) {
		if (!isEndOfString()) {
            if(regs == null) {
                regs = new Region();
            }
            byte[] ccc = ByteList.plain(string);
            if(pattern.matcher(ccc,pos,ccc.length).search(pos,ccc.length,regs, Option.NONE) >= 0) {
                lastPos = pos;
                matchStart = regs.beg[0]+pos;
                matchEnd = regs.end[0]+pos;
                pos = matchEnd;
                return string.subSequence(lastPos, pos);
            } else {
                lastPos = -1;
                resetMatchData();
            }
		}
		
		return null;
	}
