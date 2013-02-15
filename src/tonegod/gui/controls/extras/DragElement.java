/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.controls.extras;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;
import tonegod.gui.listeners.MouseButtonListener;

/**
 *
 * @author t0neg0d
 */
public abstract class DragElement extends Element implements MouseButtonListener {
	
	private Vector2f originalPosition;
	private boolean useSpringBack = false;
	private boolean useSpringBackEffect = false;
	private boolean lockToDropElementCenter = false;
	private boolean useLockToDropElementEffect = false;
	private boolean isEnabled = true;
	
	private Effect slideTo;
	
	private List<Element> dropElements = new ArrayList();
	
	public DragElement(Screen screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		
		this.originalPosition = getPosition().clone();
		originalPosition.setY(screen.getHeight()-originalPosition.getY()-getHeight());
		this.setIsMovable(true);
		this.setIsDragDropDragElement(true);
		this.setScaleNS(false);
		this.setScaleEW(false);
		
	//	this.setFontSize(screen.getStyle("Button").getFloat("fontSize"));
	//	this.setFontColor(screen.getStyle("Button").getColorRGBA("fontColor"));
	//	this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Button").getString("textVAlign")));
	//	this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Button").getString("textAlign")));
	//	this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Button").getString("textWrap")));
		
		
		
	}
	
	/**
	 * Adds an element to the list of drop elements and flags it as such
	 * @param element The element to add
	 */
	public void addDropElement(Element element) {
		element.setIsDragDropDropElement(true);
		dropElements.add(element);
	}
	
	/**
	 * Returns all drop  elements associated with the DragElement
	 * @return dropElements
	 */
	public List<Element> getDropElements() {
		return this.dropElements;
	}
	
	/**
	 * Returns the drop element at the specified index.  null if not found
	 * @param index int
	 * @return Element 
	 */
	public Element getDropElement(int index) {
		if (index > -1 && index < dropElements.size()) {
			return dropElements.get(index);
		} else {
			return null;
		}
	}
	
	/**
	 * Remove the drop element at the provided index
	 * @param index int
	 */
	public void removeDropElement(int index) {
		if (index > -1 && index < dropElements.size()) {
			dropElements.remove(index);
		}
	}
	
	/**
	 * Removes the provided element from the list of drop elements for this DragElement
	 * @param element Element
	 */
	public void removeDropElement(Element element) {
		dropElements.remove(element);
	}
	
	/**
	 * Returns the DragElement's original position
	 * @return Vector2f originalPosition
	 */
	/*
	public Vector2f getOriginalPosition() {
		return this.originalPosition;
	}
	*/
	/**
	 * Enables/disables the DragElement
	 * @param isEnabled boolean
	 */
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		if (isEnabled)
			this.setIsMovable(true);
		else
			this.setIsMovable(false);
	}
	
	/**
	 * Returns if the DragElement is current enabled/disabled
	 * @return boolean isEnabled
	 */
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	/**
	 * Set whether or not the DragElement should center itself within the drop element
	 * @param lockToDropElementCenter boolean
	 */
	public void setUseLockToDropElementCenter(boolean lockToDropElementCenter) {
		this.lockToDropElementCenter = lockToDropElementCenter;
	}
	
	/**
	 * Returns if the DragElement should center itself within the drop element
	 * @return boolean
	 */
	public boolean getUseLockToDropElementCenter() {
		return this.lockToDropElementCenter;
	}
	
	/**
	 * Enables/disables the use of the SlideTo Effect when centering within the drop element.
	 * @param useLockToDropElementEffect boolean
	 */
	public void setUseLockToDropElementEffect(boolean useLockToDropElementEffect) {
		this.useLockToDropElementEffect = useLockToDropElementEffect;
	}
	
	/**
	 * Returns if the SlideTo Effect is enabled/disabled when centering within a drop element
	 * @return 
	 */
	public boolean getUseLockToDropElementEffect() {
		return this.useLockToDropElementEffect;
	}
	
	/**
	 * Enables/disables springback to original position when dropped outside of a valid drop element
	 * @param useSpringBack boolean
	 */
	public void setUseSpringBack(boolean useSpringBack) {
		this.useSpringBack = useSpringBack;
	}
	
	/**
	 * Returns if springback is enabled for springback to original position when dropped outside of a valid drop element
	 * @return boolean
	 */
	public boolean getUseSpringBack() {
		return this.useSpringBack;
	}
	
	/**
	 * Enables/disables the use of SlideTo Effect when springback is enabled
	 * @param useSpringBackEffect boolean
	 */
	public void setUseSpringBackEffect(boolean useSpringBackEffect) {
		this.useSpringBackEffect = useSpringBackEffect;
	}
	
	/**
	 * Returns if SpringBack Effects are enabled/disabled
	 * @return boolean
	 */
	public boolean getUseSpringBackEffect() {
		return this.useSpringBackEffect;
	}
	
	@Override
	public void onMouseLeftPressed(MouseButtonEvent evt) {
		
		onDragStart(evt);
	}
	@Override
	public void onMouseLeftReleased(MouseButtonEvent evt) {
		Element dropEl = screen.getDropElement();
		int index = -1;
		
		if (dropEl != null) {
			if (dropElements.contains(dropEl)) {
				if (lockToDropElementCenter) {
					Vector2f destination = new Vector2f(
						dropEl.getAbsoluteWidth()-(dropEl.getWidth()/2)-(getWidth()/2),
						dropEl.getAbsoluteHeight()-(dropEl.getHeight()/2)-(getHeight()/2)
					);
					if (useLockToDropElementEffect) {
						slideTo = new Effect(Effect.EffectType.SlideTo, Effect.EffectEvent.Release, .15f);
						slideTo.setElement(this);
						slideTo.setEffectDestination(destination);
						screen.getEffectManager().applyEffect(slideTo);
					} else {
						setPosition(destination);
					}
				}
			} else {
				dropEl = null;
				if (useSpringBack) {
					Vector2f destination = originalPosition.clone();
					if (useSpringBackEffect) {
						slideTo = new Effect(Effect.EffectType.SlideTo, Effect.EffectEvent.Release, .15f);
						slideTo.setElement(this);
						slideTo.setEffectDestination(destination);
						screen.getEffectManager().applyEffect(slideTo);
					} else {
						setPosition(destination);
					}
				}
			}
		} else {
			if (useSpringBack) {
				Vector2f destination = originalPosition.clone();
				if (useSpringBackEffect) {
					slideTo = new Effect(Effect.EffectType.SlideTo, Effect.EffectEvent.Release, .15f);
					slideTo.setElement(this);
					slideTo.setEffectDestination(destination);
					screen.getEffectManager().applyEffect(slideTo);
				} else {
					setPosition(destination);
				}
			}
		}
		
		onDragEnd(evt, index, dropEl);
	}
	@Override
	public void onMouseRightPressed(MouseButtonEvent evt) {  }
	@Override
	public void onMouseRightReleased(MouseButtonEvent evt) {  }
	
	public abstract void onDragStart(MouseButtonEvent evt);
	public abstract void onDragEnd(MouseButtonEvent evt, int index, Element dropElement);
}