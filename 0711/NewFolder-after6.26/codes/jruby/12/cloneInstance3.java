	public CharSequence check(Regex pattern) {
		if (!isEndOfString()) {
            if(regs == null) {
                regs = new Region();
            }
            byte[] ccc = ByteList.plain(string);
            if(pattern.matcher(ccc,pos,ccc.length).search(pos,ccc.length,regs, Option.NONE) == 0) {
                matchStart = pos;
                matchEnd = regs.end[0]+pos;
                return string.subSequence(regs.beg[0]+pos,regs.end[0]+pos);
            } else {
                resetMatchData();
            }
		}
		
		return null;
	}
