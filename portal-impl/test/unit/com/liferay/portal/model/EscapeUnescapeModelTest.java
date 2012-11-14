/**
 * 
 */
package com.liferay.portal.model;

import com.liferay.portal.kernel.test.TestCase;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.util.HtmlImpl;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.model.impl.BlogsEntryImpl;

import org.junit.Before;
import org.junit.Test;



/**
 * @author Kocsis Norbert
 *
 */
public class EscapeUnescapeModelTest extends TestCase{

	private static final String _dangerousText = "Dangerou's text";
	private BlogsEntry _entry;

	@Before
	public void setUp(){
		new HtmlUtil().setHtml(new HtmlImpl());

		_entry = new BlogsEntryImpl();
		_entry.setTitle(_dangerousText);
	}

	@Test
	public void testEscape() {
		_entry = _entry.toEscapedModel();

		String escapedText = HtmlUtil.escape(_dangerousText);

		assertEquals(escapedText, _entry.getTitle());
	}

	@Test
	public void testUnescape() {
		_entry = _entry.toEscapedModel();

		if(_dangerousText.equals(_entry.getTitle())){
			fail("Test inconclusive because toEscapeModel did nothing.");
		}
		else {
			_entry = _entry.toUnescapedModel();

			assertEquals(_dangerousText, _entry.getTitle());
		}
	}
}
