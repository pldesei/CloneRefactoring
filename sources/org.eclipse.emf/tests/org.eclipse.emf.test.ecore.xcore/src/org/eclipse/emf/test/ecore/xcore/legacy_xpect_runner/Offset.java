package org.eclipse.emf.test.ecore.xcore.legacy_xpect_runner;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parsetree.reconstr.impl.NodeIterator;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;
import org.junit.Assert;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * use org.xpect.xtext.lib.setup.ThisOffset or
 * org.xpect.parameter.ParameterParser with token rule OFFSET from
 * www.xpect-tests.org instead. ThisOffset is an annotation that can be used for
 * method parameters. ParameterParser is a an annotation to define syntax for
 * test-parameters that can be specified in the DSL file (example: XPECT
 * myTestMethod myparam1 myparam2). The method parameter type can be any of the
 * following types int, INode, EObject, ICrossEReferenceAndEObject,
 * IEAttributeAndEObject, IEReferenceAndEObject, IEStructuralFeatureAndEObject,
 * etc.
 * 
 * This class will be removed in the next release after 2.4.2
 * 
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class Offset {
	protected int offset;
	protected XtextResource resource;

	public Offset(XtextResource resource, int offset) {
		super();
		this.resource = resource;
		this.offset = offset;
	}

	public EObject getEObject() {
		EObject object = NodeModelUtils
				.findActualSemanticObjectFor(getLeafNodeAtOffset());
		Assert.assertNotNull("No EObject found at offset " + offset, object);
		return object;
	}

	public Pair<EObject, EStructuralFeature> getEStructuralFeatureByOffset() {
		return getEStructuralFeatureByOffset(Predicates
				.<EStructuralFeature> alwaysTrue());
	}

	public Pair<EObject, EStructuralFeature> getEStructuralFeatureByOffset(
			Predicate<EStructuralFeature> matches) {
		INode leaf = getLeafNodeAtOffset();
		NodeIterator ni = null;
		while (ni == null || ni.hasNext()) {
			INode next = ni == null ? leaf : ni.next();
			if (ni == null)
				ni = new NodeIterator(leaf);
			Assignment ass = GrammarUtil.containingAssignment(next
					.getGrammarElement());
			if (ass != null) {
				EObject object = NodeModelUtils
						.findActualSemanticObjectFor(next);
				EStructuralFeature feat = object.eClass()
						.getEStructuralFeature(ass.getFeature());
				if (feat != null && matches.apply(feat))
					return Tuples.create(object, feat);
			}
		}
		Assert.fail("No EStructuralFeature found at offset " + offset);
		return null;
	}

	public Pair<EObject, EStructuralFeature> getEStructuralFeatureByParent() {
		INode leaf = getLeafNodeAtOffset();
		EObject object = NodeModelUtils.findActualSemanticObjectFor(leaf);
		Assert.assertNotNull("No EObject found at offset " + offset, object);
		Assignment ass = GrammarUtil.containingAssignment(leaf
				.getGrammarElement());
		while (ass == null && leaf.getParent() != null) {
			leaf = leaf.getParent();
			ass = GrammarUtil.containingAssignment(leaf.getGrammarElement());
		}
		Assert.assertNotNull("No Assignment found at offset " + offset, ass);
		EStructuralFeature feature = object.eClass().getEStructuralFeature(
				ass.getFeature());
		return Tuples.create(object, feature);
	}

	public ILeafNode getLeafNodeAtOffset() {
		ILeafNode node = NodeModelUtils.findLeafNodeAtOffset(resource
				.getParseResult().getRootNode(), offset);
		Assert.assertNotNull("No Leaf Node found at offset " + offset, node);
		return node;
	}

	@Override
	public int hashCode() {
		return offset * (resource != null ? resource.hashCode() : 1);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass())
			return false;
		Offset off = (Offset) obj;
		return resource == off.resource && offset == off.offset;
	}

	public int getOffset() {
		return offset;
	}

	public XtextResource getResource() {
		return resource;
	}

	@Override
	public String toString() {
		if (resource == null)
			return "(resource is null)";
		if (resource.getParseResult() == null
				|| resource.getParseResult().getRootNode() == null)
			return "(resource hs no parse result)";
		String text = resource.getParseResult().getRootNode().getText();
		if (offset < 0 || offset > text.length())
			return "(offset out of range)";
		int from = Math.max(0, offset - 5);
		int to = Math.min(text.length(), offset + 5);
		return text.substring(from, offset) + "!" + text.substring(offset, to);
	}
}
