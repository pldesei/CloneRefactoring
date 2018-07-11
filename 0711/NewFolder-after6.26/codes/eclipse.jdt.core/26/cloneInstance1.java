final public void getstatic(FieldBinding fieldBinding) {
	if (DEBUG) System.out.println(position + "\t\tgetstatic:"+fieldBinding); //$NON-NLS-1$
	countLabels = 0;
	if ((fieldBinding.type.id == T_double) || (fieldBinding.type.id == T_long))
		stackDepth += 2;
	else
		stackDepth += 1;
	if (stackDepth > stackMax)
		stackMax = stackDepth;
	if (classFileOffset + 2 >= bCodeStream.length) {
		resizeByteArray();
	}
	position++;
	bCodeStream[classFileOffset++] = OPC_getstatic;
	writeUnsignedShort(constantPool.literalIndex(fieldBinding));
}
