final public void putstatic(FieldBinding fieldBinding) {
	if (DEBUG) System.out.println(position + "\t\tputstatic:"+fieldBinding); //$NON-NLS-1$
	countLabels = 0;
	int id;
	if (((id = fieldBinding.type.id) == T_double) || (id == T_long))
		stackDepth -= 2;
	else
		stackDepth -= 1;
	if (stackDepth > stackMax)
		stackMax = stackDepth;
	if (classFileOffset + 2 >= bCodeStream.length) {
		resizeByteArray();
	}
	position++;
	bCodeStream[classFileOffset++] = OPC_putstatic;
	writeUnsignedShort(constantPool.literalIndex(fieldBinding));
}
