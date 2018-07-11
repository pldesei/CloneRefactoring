final public void putfield(FieldBinding fieldBinding) {
	if (DEBUG) System.out.println(position + "\t\tputfield:"+fieldBinding); //$NON-NLS-1$
	countLabels = 0;
	int id;
	if (((id = fieldBinding.type.id) == T_double) || (id == T_long))
		stackDepth -= 3;
	else
		stackDepth -= 2;
	if (stackDepth > stackMax)
		stackMax = stackDepth;
	if (classFileOffset + 2 >= bCodeStream.length) {
		resizeByteArray();
	}
	position++;
	bCodeStream[classFileOffset++] = OPC_putfield;
	writeUnsignedShort(constantPool.literalIndex(fieldBinding));
}
