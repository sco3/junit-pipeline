package pipeline;

import static java.lang.System.out;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.lesfurets.jenkins.unit.BasePipelineTest;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class TestShell extends BasePipelineTest {
	private static final String SCRIPT = "script";

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
	public void testPipelineOk() {
		Script script = loadScript("testShell.groovy");
		Binding bind = new Binding();
		bind.setVariable(SCRIPT, script);
		GroovyShell grShell = new GroovyShell(bind);
		grShell.evaluate(SCRIPT + ".call(11)");
		printCallStack();
	}

	@Test(expected = Exception.class)
	public void testPipelineFail() {
		Script script = loadScript("testShell.groovy");
		Binding bind = new Binding();
		bind.setVariable(SCRIPT, script);
		GroovyShell grShell = new GroovyShell(bind);
		grShell.evaluate(SCRIPT + ".call(12)");
	}
	
	
}
