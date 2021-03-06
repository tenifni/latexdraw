package test.gui;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import net.sf.latexdraw.glib.models.interfaces.shape.BorderPos;
import net.sf.latexdraw.glib.models.interfaces.shape.LineStyle;
import net.sf.latexdraw.instruments.ShapeBorderCustomiser;

import org.junit.Before;

public abstract class TestLineStyleGUI extends TestShapePropGUI<ShapeBorderCustomiser> {
	protected Spinner<Double> thicknessField;
	protected ColorPicker lineColButton;
	protected ComboBox<LineStyle> lineCB;
	protected ComboBox<BorderPos> bordersPosCB;
	protected Spinner<Double> frameArcField;
	protected CheckBox showPoints;

	protected final GUIVoidCommand pickLineCol = () -> pickColour(lineColButton);
	protected final GUIVoidCommand checkShowPts = () -> clickOn(showPoints);
	protected final GUIVoidCommand selectLineStyle = () -> selectNextComboBoxItem(lineCB);
	protected final GUIVoidCommand selectBorderPos = () -> selectNextComboBoxItem(bordersPosCB);
	protected final GUIVoidCommand incrementThickness = () -> incrementSpinner(thicknessField);
	protected final GUIVoidCommand incrementFrameArc = () -> incrementSpinner(frameArcField);

	@Override
	public String getFXMLPathFromLatexdraw() {
		return "/fxml/LineStyle.fxml";
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		thicknessField = find("#thicknessField");
		lineColButton = find("#lineColButton");
		lineCB = find("#lineCB");
		bordersPosCB = find("#bordersPosCB");
		frameArcField = find("#frameArcField");
		showPoints = find("#showPoints");
		ins = (ShapeBorderCustomiser)guiceFactory.call(ShapeBorderCustomiser.class);
		ins.setActivated(true);
	}
}
