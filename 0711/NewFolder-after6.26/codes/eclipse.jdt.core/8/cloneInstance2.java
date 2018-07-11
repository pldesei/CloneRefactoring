		if (unnecessaryLeftCast || unnecessaryRightCast) {
			int alternateLeftId = unnecessaryLeftCast ? ((CastExpression)left).expression.resolvedType.id : leftType.id;
			int alternateRightId = unnecessaryRightCast ? ((CastExpression)right).expression.resolvedType.id : rightType.id;
			int alternateResult = ResolveTypeTables[EQUAL_EQUAL][(alternateLeftId << 4) + alternateRightId];
			// (cast)  left   Op (cast)  right --> result
			//  1111   0000       1111   0000     1111
			//  <<16   <<12       <<8    <<4       <<0
			final int CompareMASK = (0xF<<16) + (0xF<<8) + 0xF; // mask hiding compile-time types
			if ((result & CompareMASK) == (alternateResult & CompareMASK)) { // same promotions and result
				if (unnecessaryLeftCast) scope.problemReporter().unnecessaryCast((CastExpression)left); 
				if (unnecessaryRightCast) scope.problemReporter().unnecessaryCast((CastExpression)right);
			}
		}		
