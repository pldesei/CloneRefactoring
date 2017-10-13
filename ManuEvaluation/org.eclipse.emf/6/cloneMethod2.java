This clone method is located in File: plugins/org.eclipse.emf.ecore.xcore.ui/src-gen/org/eclipse/emf/ecore/xcore/ui/contentassist/antlr/internal/InternalXcoreParser.java
The line range of this clone method is: 74429-74669
The content of this clone method is as follows:
    public final void rule__XReference__UnorderedGroup_9__Impl() throws RecognitionException {
        		int stackSize = keepStackSize();
        		boolean selected = false;
        try {
            // InternalXcore.g:25897:1: ( ( ({...}? => ( ( ( rule__XReference__Group_9_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_3__0 ) ) ) ) ) )
            // InternalXcore.g:25898:3: ( ({...}? => ( ( ( rule__XReference__Group_9_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_3__0 ) ) ) ) )
            {
            // InternalXcore.g:25898:3: ( ({...}? => ( ( ( rule__XReference__Group_9_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XReference__Group_9_3__0 ) ) ) ) )
            int alt219=4;
            int LA219_0 = input.LA(1);
            if ( LA219_0 == 20 && getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 0) ) {
                alt219=1;
            }
            else if ( LA219_0 == 22 && getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 1) ) {
                alt219=2;
            }
            else if ( LA219_0 == 21 && getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 2) ) {
                alt219=3;
            }
            else if ( LA219_0 == 23 && getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 3) ) {
                alt219=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 219, 0, input);
                throw nvae;
            }
            switch (alt219) {
                case 1 :
                    // InternalXcore.g:25900:4: ({...}? => ( ( ( rule__XReference__Group_9_0__0 ) ) ) )
                    {
                    // InternalXcore.g:25900:4: ({...}? => ( ( ( rule__XReference__Group_9_0__0 ) ) ) )
                    // InternalXcore.g:25901:5: {...}? => ( ( ( rule__XReference__Group_9_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 0) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XReference__UnorderedGroup_9__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 0)");
                    }
                    // InternalXcore.g:25901:107: ( ( ( rule__XReference__Group_9_0__0 ) ) )
                    // InternalXcore.g:25902:6: ( ( rule__XReference__Group_9_0__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 0);
                    selected = true;
                    // InternalXcore.g:25908:6: ( ( rule__XReference__Group_9_0__0 ) )
                    // InternalXcore.g:25910:7: ( rule__XReference__Group_9_0__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXReferenceAccess().getGroup_9_0()); 
                    }
                    // InternalXcore.g:25911:7: ( rule__XReference__Group_9_0__0 )
                    // InternalXcore.g:25911:8: rule__XReference__Group_9_0__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XReference__Group_9_0__0();
                    state._fsp--;
                    if (state.failed) return ;
                    }
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXReferenceAccess().getGroup_9_0()); 
                    }
                    }
                    }
                    }
                    }
                    break;
                case 2 :
                    // InternalXcore.g:25917:4: ({...}? => ( ( ( rule__XReference__Group_9_1__0 ) ) ) )
                    {
                    // InternalXcore.g:25917:4: ({...}? => ( ( ( rule__XReference__Group_9_1__0 ) ) ) )
                    // InternalXcore.g:25918:5: {...}? => ( ( ( rule__XReference__Group_9_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 1) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XReference__UnorderedGroup_9__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 1)");
                    }
                    // InternalXcore.g:25918:107: ( ( ( rule__XReference__Group_9_1__0 ) ) )
                    // InternalXcore.g:25919:6: ( ( rule__XReference__Group_9_1__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 1);
                    selected = true;
                    // InternalXcore.g:25925:6: ( ( rule__XReference__Group_9_1__0 ) )
                    // InternalXcore.g:25927:7: ( rule__XReference__Group_9_1__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXReferenceAccess().getGroup_9_1()); 
                    }
                    // InternalXcore.g:25928:7: ( rule__XReference__Group_9_1__0 )
                    // InternalXcore.g:25928:8: rule__XReference__Group_9_1__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XReference__Group_9_1__0();
                    state._fsp--;
                    if (state.failed) return ;
                    }
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXReferenceAccess().getGroup_9_1()); 
                    }
                    }
                    }
                    }
                    }
                    break;
                case 3 :
                    // InternalXcore.g:25934:4: ({...}? => ( ( ( rule__XReference__Group_9_2__0 ) ) ) )
                    {
                    // InternalXcore.g:25934:4: ({...}? => ( ( ( rule__XReference__Group_9_2__0 ) ) ) )
                    // InternalXcore.g:25935:5: {...}? => ( ( ( rule__XReference__Group_9_2__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 2) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XReference__UnorderedGroup_9__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 2)");
                    }
                    // InternalXcore.g:25935:107: ( ( ( rule__XReference__Group_9_2__0 ) ) )
                    // InternalXcore.g:25936:6: ( ( rule__XReference__Group_9_2__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 2);
                    selected = true;
                    // InternalXcore.g:25942:6: ( ( rule__XReference__Group_9_2__0 ) )
                    // InternalXcore.g:25944:7: ( rule__XReference__Group_9_2__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXReferenceAccess().getGroup_9_2()); 
                    }
                    // InternalXcore.g:25945:7: ( rule__XReference__Group_9_2__0 )
                    // InternalXcore.g:25945:8: rule__XReference__Group_9_2__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XReference__Group_9_2__0();
                    state._fsp--;
                    if (state.failed) return ;
                    }
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXReferenceAccess().getGroup_9_2()); 
                    }
                    }
                    }
                    }
                    }
                    break;
                case 4 :
                    // InternalXcore.g:25951:4: ({...}? => ( ( ( rule__XReference__Group_9_3__0 ) ) ) )
                    {
                    // InternalXcore.g:25951:4: ({...}? => ( ( ( rule__XReference__Group_9_3__0 ) ) ) )
                    // InternalXcore.g:25952:5: {...}? => ( ( ( rule__XReference__Group_9_3__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 3) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XReference__UnorderedGroup_9__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 3)");
                    }
                    // InternalXcore.g:25952:107: ( ( ( rule__XReference__Group_9_3__0 ) ) )
                    // InternalXcore.g:25953:6: ( ( rule__XReference__Group_9_3__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXReferenceAccess().getUnorderedGroup_9(), 3);
                    selected = true;
                    // InternalXcore.g:25959:6: ( ( rule__XReference__Group_9_3__0 ) )
                    // InternalXcore.g:25961:7: ( rule__XReference__Group_9_3__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXReferenceAccess().getGroup_9_3()); 
                    }
                    // InternalXcore.g:25962:7: ( rule__XReference__Group_9_3__0 )
                    // InternalXcore.g:25962:8: rule__XReference__Group_9_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XReference__Group_9_3__0();
                    state._fsp--;
                    if (state.failed) return ;
                    }
                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXReferenceAccess().getGroup_9_3()); 
                    }
                    }
                    }
                    }
                    }
                    break;
            }
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            	if (selected)
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXReferenceAccess().getUnorderedGroup_9());
            	restoreStackSize(stackSize);
        }
        return ;
    }
