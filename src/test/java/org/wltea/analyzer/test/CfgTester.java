/**
 * 
 */
package org.wltea.analyzer.test;

import org.wltea.analyzer.cfg.Configuration;

/**
 * @author Administrator
 *
 */
public class CfgTester{
	
	public static void main(String[] args){
		System.out.println(Configuration.getExtDictionarys().size());
		System.out.println(Configuration.getExtStopWordDictionarys().size());
	}

}
