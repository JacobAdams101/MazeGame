
package twizt;


import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;


/**
 * 
 * @author jacob
 */
public class KeyPressedEvent
{
    KeyboardFocusManager keyManager;

    public boolean forward = false;
    public boolean backward = false;
    public boolean strafeleft = false;
    public boolean straferight = false;
    
    public boolean lookLeft = false;
    public boolean lookRight = false;
    
    public boolean shift = false;
    
    int lastKeyCode;
    public KeyPressedEvent() {
        
        keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyManager.addKeyEventDispatcher(new KeyEventDispatcher()
        {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e)
            {
                //Test when keys are pressed
                if(e.getID()==KeyEvent.KEY_PRESSED)
                {
                    lastKeyCode = e.getKeyCode();
                    updateKey(e.getKeyCode(), true);
                    return true;
                }
                //Test when keys are released
                if(e.getID()==KeyEvent.KEY_RELEASED)
                {
                    lastKeyCode = e.getKeyCode();
                    updateKey(e.getKeyCode(), false);
                    return true;
                }
                return false;
              }
            });
    }

    private void updateKey(int key, boolean onOff) 
    {
        switch (key)
        {
            case KeyEvent.VK_LEFT:
                lookLeft = onOff;
                break;
            case KeyEvent.VK_RIGHT:
                lookRight = onOff;
                break;
            case KeyEvent.VK_W:
                forward = onOff;
                break;
            case KeyEvent.VK_S:
                backward = onOff;
                break;
            case KeyEvent.VK_A:
                strafeleft = onOff;
                break;
            case KeyEvent.VK_D:
                straferight = onOff;
                break;
            case KeyEvent.VK_SHIFT:
                shift = onOff;
                break;
            default:
                break;
        }
    }
}
