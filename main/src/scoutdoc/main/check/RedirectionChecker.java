/*******************************************************************************
 * Copyright (c) 2012 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/

package scoutdoc.main.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

import scoutdoc.main.mediawiki.ContentFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

/**
 * Check if a redirection links to an other redirection (warning or error in case of circular redirection).
 */
public class RedirectionChecker {

	public static List<Check> check(Page page) {
		Page firstRedirection = ContentFileUtility.checkRedirection(page, true);
		if(firstRedirection != null) {
			Page redirection = ContentFileUtility.checkRedirection(firstRedirection, true);
			if(redirection != null) {
				List<Page> redirections = new ArrayList<Page>();
				redirections.add(page);
				redirections.add(firstRedirection);
				while (redirection !=null) {
					if(redirections.contains(redirection)) {
						if(!redirections.get(redirections.size() - 1).equals(redirection)) {
							redirections.add(redirection);
						}
						//Circular Redirection
						Check check = new Check();
						
						check.setFileName(PageUtility.toFilePath(page));
						check.setLine(1);
						check.setColumn(1);
						check.setSeverity(Severity.error);
						check.setMessage("CIRCULAR REDIRECTION: "+ getRedirectionsPath(redirections));
						return Collections.singletonList(check);	
					}
					redirections.add(redirection);
					redirection = ContentFileUtility.checkRedirection(redirection, true);
				}
				Check check = new Check();
				check.setFileName(PageUtility.toFilePath(page));
				check.setLine(1);
				check.setColumn(1);
				check.setSeverity(Severity.warning);
				check.setMessage("DOUBLE REDIRECTION: " + getRedirectionsPath(redirections));
				return Collections.singletonList(check);				

			}
		}
		return Collections.emptyList();
	}

	private static String getRedirectionsPath(List<Page> redirections) {
		List<String> names = new ArrayList<String>(); 
		for (Page page : redirections) {
			names.add("'"+PageUtility.toFullPageName(page)+"'");
		}
		return Joiner.on(" => ").join(names);
	}

}