package avalon.tutorial;

import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.LogEnabled;

public interface BirdSong extends Parameterizable, Startable, LogEnabled
{
	public static final String PARAM_NUMBER_OF_CHILPS = "numberOfChilps";
	public static final String PARAM_CHILP_MESSAGE = "chilpMessage";
	public static final String PARAM_CHILP_SEPARATOR = "chilpSeparator";

	/**
	* Provide us with the parameters it needs to work. Required are:
	* <ul>
	* <li>numberOfChilps</li>
	* <li>chilpMessage</li>
	* <li>chilpSeparator</li>
	* </ul>
	* </pre>
	*
	*/
	void parameterize( Parameters parameters ) throws ParameterException;
}

