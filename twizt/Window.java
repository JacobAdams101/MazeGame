
package twizt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static twizt.Twizt.FUNVALUE;

/**
 *
 * @author jacob
 */
public class Window extends JPanel
{
    private JFrame frame;
    
    static NoiseGenerator noise = new NoiseGenerator();
    
    
    static class NoiseGenerator
    {
        private BufferedImage noise;
        
        public NoiseGenerator()
        {
            load();
        }
        
        private void load()
        {
            try
            {
                System.out.println("Beans");

                URL url = this.getClass().getResource("Static.png");
                System.out.println("URL: " + url);
                noise = ImageIO.read(url);
            }
            catch (IOException ex)
            {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        public void addNoise(Graphics g, int width, int height)
        {
            
            int x = -(int)(Math.random()*noise.getWidth());
            int y = -(int)(Math.random()*noise.getHeight());
            
            for (int ix = 0; ix <= width/noise.getWidth(); ix++)
            {
                for (int iy = 0; iy <= 1+(height/noise.getHeight()); iy++)
                {
                    g.drawImage(noise, x + (ix * noise.getWidth()), y + (iy * noise.getHeight()), null);
                }
            }
        }
    }

    
    public Window()
    {
        //Setup the JFrame
        if (FUNVALUE < 0.3)
        {
            frame = new JFrame("MAZEGAMEMAZEGAMEMAZEGAMEMAZEGAMEMAZEGAMEMAZEGAMEMAZEGAMEMAZEGAMEMAZEGAMEMAZEGAME.exe"); //Set the frames name/title
        }
        else
        {
            frame = new JFrame("Maz3.exe"); //Set the frames name/title
        }
        
        frame.add(this); //Add my JPanel object to the frame
        //Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setSize((int) (1000), (int) (900)); //Set my size
        
	frame.setVisible(true); //Set the frame to be visible
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Stop the program when the frame is closed
        frame.setResizable(false); //Set my frame to be resizable
    }
    int count = 0;
    public void trigger()
    {
        count = 30;
    }
    
    private Camera cam;

    @Override
    public synchronized void paint(Graphics g)
    {
        if (count > 0)
        {
            count--;
            g.setColor(Color.BLACK);
            g.setXORMode(Color.WHITE);
            for (int i = 0; i < count; i++)
            {
                g.fillRect(Twizt.random(0, this.getWidth()/2), Twizt.random(0, this.getHeight()/2), Twizt.random(0, this.getWidth()/2), Twizt.random(0, this.getHeight()/2));
            }
        }
        else if (cam != null)
        {
            cam.updateFrame(); //Updates the frame to draw
            BufferedImage camFrame = cam.getFrame();
            noise.addNoise(camFrame.getGraphics(), this.getWidth(), this.getHeight());
            g.drawImage(camFrame, 0, 0, this.getWidth(), this.getHeight(), null);
            
        }
        
        
        
        
    }
    

    
    public void render(Camera cam)
    {
        this.cam = cam;
        
        this.repaint();
    }
    
}
