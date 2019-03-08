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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;

import gr.gousiosg.javacg.info.ClassInfo;
import gr.gousiosg.javacg.info.MethodInfo;
import gr.gousiosg.javacg.util.Filter;

/**
 * The simplest of class visitors, invokes the method visitor class for each
 * method found.
 */
public class ClassVisitor extends EmptyVisitor {

    private JavaClass clazz;
    private Filter filter;
    private ConstantPoolGen constants;
    private final DynamicCallManager DCManager = new DynamicCallManager();
    private ClassInfo info;
    

    public ClassVisitor(JavaClass clazz, Filter filter) {
        this.clazz = clazz;
        this.filter = filter;
        constants = new ConstantPoolGen(clazz.getConstantPool());
    }

    public void visitJavaClass(JavaClass jc) {
        jc.getConstantPool().accept(this);
        Method[] methods = jc.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            DCManager.retrieveCalls(method, jc);
            DCManager.linkCalls(method);
            method.accept(this);
        }
    }

    public void visitMethod(Method method) {
    	MethodVisitor visitor = new MethodVisitor(clazz, constants, method, filter);
    	info.addMethod(visitor.start());
    }

    public ClassInfo start() {
    	info = new ClassInfo();
    	info.setClassname(clazz.getClassName());
    	info.setSuperclasses(resolveSuper(clazz));
        visitJavaClass(clazz);
        return info;
    }

    
    public List<String> resolveSuper(JavaClass in) {

    	List<String> out = new ArrayList<String>();
    	
    	out.add(in.getSuperclassName());
    	
    	for(String i : in.getInterfaceNames()) {
    		out.add(i);
    	}

    	TreeSet<String> x = new TreeSet<String>();
    	for(String n : out) {
    		if(filter.accept(n) && filter.include(n)) {
    			x.add(n);
    			try {
					x.addAll(resolveSuper(in.getRepository().findClass(n)));
				} catch (Exception e) {
				}
    		}
    	}
    	
    	return new ArrayList<>(x);
    }
    
}
