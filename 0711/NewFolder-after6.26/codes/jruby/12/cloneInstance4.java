	public CharSequence checkUntil(Regex pattern) {
		if (!isEndOfString()) {
            if(regs == null) {
                regs = new Region();
            }
            byte[] ccc = ByteList.plain(string);
            if(pattern.matcher(ccc,pos,ccc.length).search(pos,ccc.length,regs, Option.NONE) >= 0) {
                matchStart = regs.beg[0]+pos;
                matchEnd = regs.end[0]+pos;
                return string.subSequence(pos,matchEnd);
            } else {
                resetMatchData();
            }
		}
		
		return null;
	}
