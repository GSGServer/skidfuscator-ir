package dev.skidfuscator.ir.insn.impl;

import dev.skidfuscator.ir.hierarchy.Hierarchy;
import dev.skidfuscator.ir.insn.AbstractInsn;
import dev.skidfuscator.ir.klass.KlassNode;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;

public class MultiANewArrayInsn extends AbstractInsn<MultiANewArrayInsnNode> {

    private KlassNode target;

    public MultiANewArrayInsn(Hierarchy hierarchy, MultiANewArrayInsnNode node) {
        super(hierarchy, node);
    }

    @Override
    public void resolve() {
        this.target = hierarchy.findClass(Type.getType(node.desc).getElementType().getInternalName());

        super.resolve();
    }

    @Override
    public MultiANewArrayInsnNode dump() {
        //this.node.desc = "[".repeat(node.dims) + "L" + target.getName() + ";"; //I have no idea
        return super.dump();
    }
}
