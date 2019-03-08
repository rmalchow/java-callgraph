/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gr.gousiosg.javacg.stat;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

import gr.gousiosg.javacg.info.CallType;
import gr.gousiosg.javacg.info.MethodCall;
import gr.gousiosg.javacg.info.MethodInfo;
import gr.gousiosg.javacg.util.Filter;

/**
 * The simplest of method visitors, prints any invoked method
 * signature for all method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitor extends EmptyVisitor {

    private JavaClass clazz;
    private ConstantPoolGen constants;
    private Method method;
    private MethodGen mg;
    private Filter filter;
    private MethodInfo mi = new MethodInfo();

    public MethodVisitor(JavaClass clazz, ConstantPoolGen constants, Method method, Filter filter) {
    	this.clazz = clazz;
    	this.constants = constants;
    	this.method = method;
    	this.filter = filter;
    	this.mg = new  MethodGen(method, clazz.getClassName(), constants);
	}

    public MethodInfo start() {

    	mi.setName(method.getName());
    	mi.setSignature(method.getSignature());
    	
        if (mg.isAbstract() || mg.isNative()) return mi;

        for (InstructionHandle ih = mg.getInstructionList().getStart(); 
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            
            if (!visitInstruction(i))
                i.accept(this);
        }
        
        return mi;
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        return ((InstructionConst.getInstruction(opcode) != null)
                && !(i instanceof ConstantPushInstruction) 
                && !(i instanceof ReturnInstruction));
    }
    
    private String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        return sb.toString();
    }
    

    private void visitInternal(CallType ct, InvokeInstruction i) {
    	
    	String callee = i.getReferenceType(constants).toString();
    	
    	if(!filter.accept(callee)) {
    		return;
    	}
    	if(!filter.include(callee)) {
    		return;
    	}
    	
    	MethodCall mc = new MethodCall();
    	mc.setCaller(clazz.getClassName());
    	mc.setCallerMethod(method.getName());
    	mc.setCallee(i.getReferenceType(constants).toString());
    	mc.setCalleeMethod(i.getMethodName(constants));
    	mc.setArgumentSignature(argumentList(i.getArgumentTypes(constants)));
    	mi.addCall(mc);
    	
    }
    
    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
    	visitInternal(CallType.invokevirtual, i);
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
    	visitInternal(CallType.invokeinterface, i);
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
    	visitInternal(CallType.invokespecial, i);
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
    	visitInternal(CallType.invokestatic, i);
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
    	visitInternal(CallType.invokedynamic, i);
    }
}
