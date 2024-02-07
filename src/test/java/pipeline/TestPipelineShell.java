package pipeline;

import static java.lang.System.out;
import static java.util.regex.Pattern.compile;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.lesfurets.jenkins.unit.BasePipelineTest;

import groovy.lang.Script;

public class TestPipelineShell extends BasePipelineTest {
	// private static final String SCRIPT = "script";

	private static final String CALL = "call";

	@Before
	@Override
	public void setUp() throws Exception {
		String[] sRoots = getScriptRoots();
		String[] roots = new String[sRoots.length + 1];
		int i = 0;
		for (String r : sRoots) {
			roots[i] = r;
			i++;
		}
		roots[i] = "src/test/groovy";
		setScriptRoots(roots);
		super.setUp();
		getHelper().addShMock(compile("(?s).*test12.*"), "cannot create dir test12", 1);
		getHelper().addShMock(compile("(?s).*test11.*"), "Dir test11 created", 0);
	}

	@Test
	public void testPattern() {

		{
			Pattern pat = Pattern.compile(".*test12.*");
			Matcher m = pat.matcher("mkdir /tmp/test12");
			out.println("Matches: " + m.matches());
		}
		{
			Pattern pat = Pattern.compile("(?s).*test12.*");
			Matcher m = pat.matcher("\nmkdir /tmp/test12\n");
			out.println("Matches: " + m.matches());
		}
	}

	@Test
	public void testPipelineOk() throws Exception {
		Script script = loadScript("testShell.groovy");
		Method call = script.getClass().getDeclaredMethod(//
				CALL, int.class //
		);
		call.invoke(script, 11);
		printCallStack();
	}

	@Test
	public void testPipelineFail() {
		Script script = loadScript("testShell.groovy");
		try {
			Method call = script.getClass().getDeclaredMethod(//
					CALL, int.class //
			);
			call.invoke(script, 12);
		} catch (Exception e) {
			String message = e.getCause().getMessage();
			assertEquals("script returned exit code 1", message);
		}

	}

}
