This clone instance is located in File: plugins/org.eclipse.emf.ecore.xcore.ui/src-gen/org/eclipse/emf/ecore/xcore/ui/contentassist/antlr/internal/InternalXcoreParser.java
The line range of this clone instance is: 73012-73252
The content of this clone instance is as follows:
    public final void rule__XAttribute__UnorderedGroup_7__Impl() throws RecognitionException {

        		int stackSize = keepStackSize();
        		boolean selected = false;
            
        try {
            // InternalXcore.g:25481:1: ( ( ({...}? => ( ( ( rule__XAttribute__Group_7_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_3__0 ) ) ) ) ) )
            // InternalXcore.g:25482:3: ( ({...}? => ( ( ( rule__XAttribute__Group_7_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_3__0 ) ) ) ) )
            {
            // InternalXcore.g:25482:3: ( ({...}? => ( ( ( rule__XAttribute__Group_7_0__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_1__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_2__0 ) ) ) ) | ({...}? => ( ( ( rule__XAttribute__Group_7_3__0 ) ) ) ) )
            int alt206=4;
            int LA206_0 = input.LA(1);

            if ( LA206_0 == 20 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 0) ) {
                alt206=1;
            }
            else if ( LA206_0 == 22 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 1) ) {
                alt206=2;
            }
            else if ( LA206_0 == 21 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 2) ) {
                alt206=3;
            }
            else if ( LA206_0 == 23 && getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 3) ) {
                alt206=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 206, 0, input);

                throw nvae;
            }
            switch (alt206) {
                case 1 :
                    // InternalXcore.g:25484:4: ({...}? => ( ( ( rule__XAttribute__Group_7_0__0 ) ) ) )
                    {
                    // InternalXcore.g:25484:4: ({...}? => ( ( ( rule__XAttribute__Group_7_0__0 ) ) ) )
                    // InternalXcore.g:25485:5: {...}? => ( ( ( rule__XAttribute__Group_7_0__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 0) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XAttribute__UnorderedGroup_7__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 0)");
                    }
                    // InternalXcore.g:25485:107: ( ( ( rule__XAttribute__Group_7_0__0 ) ) )
                    // InternalXcore.g:25486:6: ( ( rule__XAttribute__Group_7_0__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 0);
                    selected = true;
                    // InternalXcore.g:25492:6: ( ( rule__XAttribute__Group_7_0__0 ) )
                    // InternalXcore.g:25494:7: ( rule__XAttribute__Group_7_0__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXAttributeAccess().getGroup_7_0()); 
                    }
                    // InternalXcore.g:25495:7: ( rule__XAttribute__Group_7_0__0 )
                    // InternalXcore.g:25495:8: rule__XAttribute__Group_7_0__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XAttribute__Group_7_0__0();

                    state._fsp--;
                    if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXAttributeAccess().getGroup_7_0()); 
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalXcore.g:25501:4: ({...}? => ( ( ( rule__XAttribute__Group_7_1__0 ) ) ) )
                    {
                    // InternalXcore.g:25501:4: ({...}? => ( ( ( rule__XAttribute__Group_7_1__0 ) ) ) )
                    // InternalXcore.g:25502:5: {...}? => ( ( ( rule__XAttribute__Group_7_1__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 1) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XAttribute__UnorderedGroup_7__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 1)");
                    }
                    // InternalXcore.g:25502:107: ( ( ( rule__XAttribute__Group_7_1__0 ) ) )
                    // InternalXcore.g:25503:6: ( ( rule__XAttribute__Group_7_1__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 1);
                    selected = true;
                    // InternalXcore.g:25509:6: ( ( rule__XAttribute__Group_7_1__0 ) )
                    // InternalXcore.g:25511:7: ( rule__XAttribute__Group_7_1__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXAttributeAccess().getGroup_7_1()); 
                    }
                    // InternalXcore.g:25512:7: ( rule__XAttribute__Group_7_1__0 )
                    // InternalXcore.g:25512:8: rule__XAttribute__Group_7_1__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XAttribute__Group_7_1__0();

                    state._fsp--;
                    if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXAttributeAccess().getGroup_7_1()); 
                    }

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // InternalXcore.g:25518:4: ({...}? => ( ( ( rule__XAttribute__Group_7_2__0 ) ) ) )
                    {
                    // InternalXcore.g:25518:4: ({...}? => ( ( ( rule__XAttribute__Group_7_2__0 ) ) ) )
                    // InternalXcore.g:25519:5: {...}? => ( ( ( rule__XAttribute__Group_7_2__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 2) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XAttribute__UnorderedGroup_7__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 2)");
                    }
                    // InternalXcore.g:25519:107: ( ( ( rule__XAttribute__Group_7_2__0 ) ) )
                    // InternalXcore.g:25520:6: ( ( rule__XAttribute__Group_7_2__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 2);
                    selected = true;
                    // InternalXcore.g:25526:6: ( ( rule__XAttribute__Group_7_2__0 ) )
                    // InternalXcore.g:25528:7: ( rule__XAttribute__Group_7_2__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXAttributeAccess().getGroup_7_2()); 
                    }
                    // InternalXcore.g:25529:7: ( rule__XAttribute__Group_7_2__0 )
                    // InternalXcore.g:25529:8: rule__XAttribute__Group_7_2__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XAttribute__Group_7_2__0();

                    state._fsp--;
                    if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXAttributeAccess().getGroup_7_2()); 
                    }

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // InternalXcore.g:25535:4: ({...}? => ( ( ( rule__XAttribute__Group_7_3__0 ) ) ) )
                    {
                    // InternalXcore.g:25535:4: ({...}? => ( ( ( rule__XAttribute__Group_7_3__0 ) ) ) )
                    // InternalXcore.g:25536:5: {...}? => ( ( ( rule__XAttribute__Group_7_3__0 ) ) )
                    {
                    if ( ! getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 3) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "rule__XAttribute__UnorderedGroup_7__Impl", "getUnorderedGroupHelper().canSelect(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 3)");
                    }
                    // InternalXcore.g:25536:107: ( ( ( rule__XAttribute__Group_7_3__0 ) ) )
                    // InternalXcore.g:25537:6: ( ( rule__XAttribute__Group_7_3__0 ) )
                    {
                    getUnorderedGroupHelper().select(grammarAccess.getXAttributeAccess().getUnorderedGroup_7(), 3);
                    selected = true;
                    // InternalXcore.g:25543:6: ( ( rule__XAttribute__Group_7_3__0 ) )
                    // InternalXcore.g:25545:7: ( rule__XAttribute__Group_7_3__0 )
                    {
                    if ( state.backtracking==0 ) {
                       before(grammarAccess.getXAttributeAccess().getGroup_7_3()); 
                    }
                    // InternalXcore.g:25546:7: ( rule__XAttribute__Group_7_3__0 )
                    // InternalXcore.g:25546:8: rule__XAttribute__Group_7_3__0
                    {
                    pushFollow(FollowSets000.FOLLOW_2);
                    rule__XAttribute__Group_7_3__0();

                    state._fsp--;
                    if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       after(grammarAccess.getXAttributeAccess().getGroup_7_3()); 
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
            		getUnorderedGroupHelper().returnFromSelection(grammarAccess.getXAttributeAccess().getUnorderedGroup_7());
            	restoreStackSize(stackSize);

        }
        return ;
    }
