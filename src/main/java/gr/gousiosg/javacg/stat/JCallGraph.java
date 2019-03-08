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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;

import com.fasterxml.jackson.core.JsonProcessingException;

import gr.gousiosg.javacg.info.ClassInfo;
import gr.gousiosg.javacg.output.GraphmlRenderer;
import gr.gousiosg.javacg.output.Renderer;
import gr.gousiosg.javacg.util.Filter;
import gr.gousiosg.javacg.util.FilterBuilder;

/**
 * Constructs a callgraph out of a JAR archive. Can combine multiple archives
 * into a single call graph.
 *
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class JCallGraph {

	private ClassPath createClasspath(List<String> paths) {
    	StringBuffer sb = new StringBuffer();
        for (String path : paths) {
        	sb.append(sb.length()>0?File.pathSeparator:"");
        	sb.append(path);
        }
    	return new ClassPath(ClassPath.SYSTEM_CLASS_PATH,sb.toString());
	}
	
	
	public void parse(List<String> paths, Filter filter, Renderer r, OutputStream os) {
		ClassPath cPath = createClasspath(paths);
    	Repository repo = SyntheticRepository.getInstance(cPath);
    	List<ClassInfo> cis = new ArrayList<ClassInfo>();
    	for (String path : paths) {
            File f = new File(path);
            if(!f.exists()) continue;
            if(!f.isFile()) continue;
            try (JarFile jar = new JarFile(f)) {
            	Enumeration<JarEntry> entries = jar.entries();
            	while(entries.hasMoreElements()) {
            		
            		JarEntry je = entries.nextElement();
            		if(!je.getName().endsWith(".class")) continue;
            		
            		try {
            			ClassParser cp = new ClassParser(path, je.getName());
            			JavaClass jc = cp.parse();
            			
            			if(!filter.accept(jc)) continue;
            			if(!filter.include(jc)) continue;
            			
						jc.setRepository(repo);
						ClassVisitor cv = new ClassVisitor(jc,filter);
						ClassInfo ci = cv.start();
						cis.add(ci);
					} catch (Exception e) {
		            	System.err.println("error processing class file: "+path+" / "+je.getName());
		            	e.printStackTrace();
					}
            		
            	}
            	
            } catch (Exception e) {
            	System.err.println("error processing jar file: "+path);
            	e.printStackTrace();
			}
    	} 
    	
    	try {
    		
    		r.write(cis, os);

		} catch (Exception e) {
        	System.err.println("error outputting result: ");
        	e.printStackTrace();
		}
    	
	}
	
	
	
    public static void main(String[] args) throws JsonProcessingException, IOException {
    	
    	
    	Filter f = FilterBuilder.get().include("/ *** my pattern ***/").build();

        List<String> paths = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.jar");
        Files.walk(Paths.get("/*** PATH ***/"))
        	.filter(matcher::matches)
        	.forEach(
        		p -> { paths.add(p.toAbsolutePath().toString()); }
        	);

        JCallGraph jcg = new JCallGraph();
        FileOutputStream os = new FileOutputStream("out.graphml");
        jcg.parse(paths, f, new GraphmlRenderer(), os);
        os.flush();
        

    }

}
