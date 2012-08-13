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

import org.junit.Assert;

import java.util.List;

import org.junit.Test;

import scoutdoc.main.TU;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

/**
 * test for {@link RedirectionChecker}
 */
public class RedirectionCheckerTest {

	@Test
	public void testCheckNotMatch() {
		TU.initProperties();
		
		runNotMatch(PageUtility.toPage("Test_Page1"));
		runNotMatch(PageUtility.toPage("Test_Red2"));
		runNotMatch(PageUtility.toPage("Test_Red3"));
	}

	private void runNotMatch(Page page) {
		List<Check> actual = RedirectionChecker.check(page);
		Assert.assertEquals("size", 0, actual.size());
	}
	
	@Test
	public void testCheck() {
		TU.initProperties();

		Page page = PageUtility.toPage("Test_Red1");
		
		List<Check> actual = RedirectionChecker.check(page);
		Assert.assertEquals("size", 1, actual.size());
		
		Assert.assertEquals("check file name", PageUtility.toFilePath(page), actual.get(0).getFileName());
		Assert.assertEquals("check line", 1, actual.get(0).getLine());
		Assert.assertEquals("check column", 1, actual.get(0).getColumn());
		Assert.assertEquals("check column", "DOUBLE REDIRECTION: 'Test Red1' => 'Test Red2' => 'Test Page2'", actual.get(0).getMessage());
		Assert.assertEquals("check column", Severity.warning, actual.get(0).getSeverity());
	}
	
	@Test
	public void testCheckSelfRedirection() {
		TU.initProperties();
		runCircularRedirection("'Test RedSelf' => 'Test RedSelf'", PageUtility.toPage("Test_RedSelf"));
	}
	
	@Test
	public void testCircularRedirection() {
		TU.initProperties();

		runCircularRedirection("'Test RedCirc1' => 'Test RedCirc2' => 'Test RedCirc1'", PageUtility.toPage("Test_RedCirc1"));
		runCircularRedirection("'Test RedCirc2' => 'Test RedCirc1' => 'Test RedCirc2'", PageUtility.toPage("Test_RedCirc2"));
		runCircularRedirection("'Test RedCirc3' => 'Test RedCirc2' => 'Test RedCirc1' => 'Test RedCirc2'", PageUtility.toPage("Test_RedCirc3"));
	}
	
	private void runCircularRedirection(String expectedMessagePath, Page page) {
		List<Check> actual = RedirectionChecker.check(page);
		Assert.assertEquals("size", 1, actual.size());
		
		Assert.assertEquals("check file name", PageUtility.toFilePath(page), actual.get(0).getFileName());
		Assert.assertEquals("check line", 1, actual.get(0).getLine());
		Assert.assertEquals("check column", 1, actual.get(0).getColumn());
		Assert.assertEquals("check column", "CIRCULAR REDIRECTION: " + expectedMessagePath, actual.get(0).getMessage());
		Assert.assertEquals("check column", Severity.error, actual.get(0).getSeverity());
	}
}