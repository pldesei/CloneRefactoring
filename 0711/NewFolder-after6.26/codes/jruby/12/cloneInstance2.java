	public CharSequence scan(Regex pattern) {
		if (!isEndOfString()) {
            if(regs == null) {
                regs = new Region();
            }
            byte[] ccc = ByteList.plain(string);
            if(pattern.matcher(ccc,pos,ccc.length).search(pos,ccc.length,regs, Option.NONE) == 0) {
                lastPos = pos;
                matchStart = pos;
                pos = regs.end[0]+lastPos;
                matchEnd = pos;
                return string.subSequence(regs.beg[0]+lastPos,regs.end[0]+lastPos);
            } else {
                lastPos = -1;
                resetMatchData();
            }
		}
		
		return null;
	}
