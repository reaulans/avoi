/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package avoi;

import avoi.avance.Filtre.ColorFilter;
import avoi.avance.Filtre.DinamiqueFilter;
import avoi.avance.Filtre.EgalisationNiveauGris;
import static avoi.avance.Filtre.EgalisationNiveauGris.histogrammeMatrice;
import avoi.local.transformation.Rotation;
import avoi.local.transformation.Transformation;
import avoi.local.transformation.Zoom;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;


/**
 *
 * @author faneva
 */
public class ImagView extends javax.swing.JFrame {

    /**
     * Creates new form ImagView
     */
    Image activeImage;
    int indexHistorique;
    Image initialImage;
    String activeAction;
    ArrayList<BufferedImage> historiqueTraitement  =  new ArrayList();
    private static final int BINS = 256;
    private HistogramDataset dataset;
    private XYBarRenderer renderer;
    ChartPanel panel;
    
    public ImagView() {
        initComponents();
        afficheControle(false, null, 2);
        
    }
    
    public void afficheControle(boolean visible, String action, int nombreInput){
        if(nombreInput == 2){
            firstText.setVisible(visible); 
             secondText.setVisible(visible);
        }else{
           
            firstText.setVisible(visible);
        }
        appliqueButton.setVisible(visible);
        annulerButton.setVisible(visible);
        activeAction = action;
    }
    public void afficherImageInformation(int imageHauteur, int imageLargeur, String urlImage){
            nomFichier.setText(urlImage);
            hauteurImage.setText(String.valueOf(imageHauteur));
            largeurImage.setText(String.valueOf(imageLargeur));
    }
    
    public void addHistorique(BufferedImage image){
        historiqueTraitement.add(image);
        indexHistorique = historiqueTraitement.size() -1;
    }
    
    public void repaintPanelImage(BufferedImage image){
       
        labelImage.removeAll();
        labelImage.validate();
        labelImage.repaint();
        BufferedImage buffered =  image;
        createChartPanel(image);
        rgbChartPanel.removeAll();
        rgbChartPanel.setLayout(new BorderLayout());
        rgbChartPanel.add(panel, BorderLayout.CENTER);
        rgbChartPanel.validate();
        rgbChartPanel.repaint();
        ImageIcon source = new ImageIcon(buffered);
        int width = (int) Math.round(400);
        int height = (int) Math.round(297);
        
        source = new ImageIcon(source.getImage().getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_DEFAULT));
        labelImage.setIcon(source);
        
        ColorFilter colorFilter = new ColorFilter();
        afficheImageToLabel(labelGreen, colorFilter.filterGreen(image));
        afficheImageToLabel(labelBlue, colorFilter.filterBlue(image));
        afficheImageToLabel(labelRed, colorFilter.filterRed(image));
        afficheImageToLabel(labelSepia,colorFilter.filterSepia(image,0));
        afficheImageToLabel(labelGray, filterGray(image));
        Image newImage = (Image) image;
        activeImage = newImage;
            
        }

    public Image loadFile(String name){
        Image imageLoad = null;
        try {
            imageLoad = ImageIO.read(new File(name));
        } catch (IOException ex) {
            Logger.getLogger(ImagView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageLoad;
    }
    
    public void rotationAngle(int angle){
       Rotation rotation = new Rotation();
       Image image = rotation.getRotation(activeImage, angle);
       BufferedImage buffered = (BufferedImage)image;
       repaintPanelImage(buffered);
       addHistorique(buffered);
    }
    
    public void afficheImageToLabel(JLabel label, Image image){
            BufferedImage buffered = (BufferedImage) image;
            ImageIcon source = new ImageIcon(buffered);
            int width = (int) Math.round(122);
            int height = (int) Math.round(110);
            source = new ImageIcon(source.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
            label.setIcon(source);
    }
    
    public Image filterGray(Image image){
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage buffered = (BufferedImage) image;
        BufferedImage retourBuffered = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());;
        op.filter(buffered, retourBuffered);
        return (Image) retourBuffered;
    }
    
    public void createChartPanel(BufferedImage buffered) {
        // dataset
        System.out.println("afficher le chart");
        dataset = new HistogramDataset();
        Raster raster = buffered.getRaster();
        final int w = buffered.getWidth();
        final int h = buffered.getHeight();
        double[] r = new double[w * h];
        r = raster.getSamples(0, 0, w, h, 0, r);
        dataset.addSeries("Red", r, BINS);
        r = raster.getSamples(0, 0, w, h, 1, r);
        dataset.addSeries("Green", r, BINS);
        r = raster.getSamples(0, 0, w, h, 2, r);
        dataset.addSeries("Blue", r, BINS);
        // chart
        JFreeChart chart = ChartFactory.createHistogram("Histogram", "Value",
            "Count", dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        // translucent red, green & blue
        Paint[] paintArray = {
            new Color(0x80ff0000, true),
            new Color(0x8000ff00, true),
            new Color(0x800000ff, true)
        };
        plot.setDrawingSupplier(new DefaultDrawingSupplier(
            paintArray,
            DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
        panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.validate();
        panel.repaint();
        
    }
    
//    public ChartPanel showChart( BufferedImage image){
////        dataset = new HistogramDataset();
//        Raster raster = image.getRaster();
//        final int w = image.getWidth();
//        final int h = image.getHeight();
//        double[] r = new double[h * w];
//        r = raster.getSamples(0, 0, w, h, 0, r);
////        dataset.addSeries("Rouge", r, BINS);
////        r = raster.getSamples(0, 0, w, h, 1, r);
////        dataset.addSeries("Vert", r, BINS);
////        r = raster.getSamples(0, 0, w, h, 2, r);
////        dataset.addSeries("Bleu", r, BINS);
////        JFreeChart chart = ChartFactory.createHistogram("Histogram", "value", "count", dataset,PlotOrientation.VERTICAL,true, true, false);
////        XYPlot plot = (XYPlot) chart.getPlot();
////        renderer = (XYBarRenderer) plot.getRenderer();
////        renderer.setBarPainter(new StandardXYBarPainter());
//        Paint[] paintArray = {
//            new Color(0x80ff0000,true),
//            new Color(0x8000ff00,true),
//            new Color(0x800000ff,true),
//        };
////        plot.setDrawingSupplier(new DefaultDrawingSupplier(
////        paintArray,
////        DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
////        DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
////        DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
////        DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
////        DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE
////        ));
////        chart.setBackgroundPaint(Color.BLACK);
////        ChartPanel chartPanel = new ChartPanel(chart);
////        chartPanel.setMouseWheelEnabled(true);
////        return chartPanel;
//    }
    
//    public IntervalXYDataset createDataSet(double[] greenHis,double[] redHis,double[] blueHis){
//        HistogramDataset histogrmdataset = new HistogramDataset();
//        histogrmdataset.addSeries("Rouge", redHis, 256);
//        
//        return histogrmdataset;
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jMenuItem8 = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        zoomupButton = new javax.swing.JButton();
        zoomdownButton = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        firstText = new javax.swing.JTextField();
        secondText = new javax.swing.JTextField();
        appliqueButton = new javax.swing.JButton();
        annulerButton = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        nomFichier = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        hauteurImage = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        largeurImage = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sousPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        labelImage = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        panelRed = new javax.swing.JPanel();
        labelRed = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        panelGreen = new javax.swing.JPanel();
        labelGreen = new javax.swing.JLabel();
        panelBlue = new javax.swing.JPanel();
        labelBlue = new javax.swing.JLabel();
        panelSepia = new javax.swing.JPanel();
        labelSepia = new javax.swing.JLabel();
        panelGray = new javax.swing.JPanel();
        labelGray = new javax.swing.JLabel();
        saturationSlide = new javax.swing.JSlider();
        luminositeSlide = new javax.swing.JSlider();
        contrasteSlide = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        rgbChartPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        openFileButton = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem37 = new javax.swing.JMenuItem();
        jMenuItem38 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        cadreMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenuItem36 = new javax.swing.JMenuItem();
        specifiedColeurFilterButton = new javax.swing.JMenuItem();

        jMenuItem8.setText("jMenuItem8");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setForeground(new java.awt.Color(0, 0, 0));
        setLocationByPlatform(true);

        jToolBar1.setRollover(true);

        undoButton.setText("Revenir");
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(undoButton);

        redoButton.setText("Avancer");
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(redoButton);
        jToolBar1.add(jSeparator7);

        zoomupButton.setText("Zoom +");
        zoomupButton.setFocusable(false);
        zoomupButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomupButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomupButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(zoomupButton);

        zoomdownButton.setText("Zoom -");
        zoomdownButton.setFocusable(false);
        zoomdownButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomdownButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomdownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomdownButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(zoomdownButton);
        jToolBar1.add(jSeparator8);

        firstText.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        firstText.setText("0");
        firstText.setMaximumSize(new java.awt.Dimension(100, 30));
        firstText.setMinimumSize(new java.awt.Dimension(100, 30));
        firstText.setPreferredSize(new java.awt.Dimension(100, 30));
        jToolBar1.add(firstText);

        secondText.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        secondText.setText("0");
        secondText.setMaximumSize(new java.awt.Dimension(100, 30));
        secondText.setMinimumSize(new java.awt.Dimension(100, 30));
        secondText.setPreferredSize(new java.awt.Dimension(100, 30));
        jToolBar1.add(secondText);

        appliqueButton.setText("Vrai");
        appliqueButton.setFocusable(false);
        appliqueButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        appliqueButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        appliqueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appliqueButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(appliqueButton);

        annulerButton.setText("Annuler");
        annulerButton.setFocusable(false);
        annulerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        annulerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        annulerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annulerButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(annulerButton);
        jToolBar1.add(jSeparator9);

        nomFichier.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        nomFichier.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nomFichier.setText("Fichier");
        jToolBar1.add(nomFichier);
        jToolBar1.add(jSeparator10);

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel2.setText("  H: ");
        jToolBar1.add(jLabel2);

        hauteurImage.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        hauteurImage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        hauteurImage.setText("0");
        jToolBar1.add(hauteurImage);

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel1.setText("  L: ");
        jToolBar1.add(jLabel1);

        largeurImage.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        largeurImage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        largeurImage.setText("0");
        largeurImage.setToolTipText("");
        jToolBar1.add(largeurImage);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(600, 300));

        sousPanel.setBackground(new java.awt.Color(0, 0, 0));
        sousPanel.setMaximumSize(null);
        sousPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));

        labelImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelImage.setText("jLabel3");
        labelImage.setMaximumSize(null);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelImage, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelImage, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane3.setViewportView(jPanel3);

        javax.swing.GroupLayout sousPanelLayout = new javax.swing.GroupLayout(sousPanel);
        sousPanel.setLayout(sousPanelLayout);
        sousPanelLayout.setHorizontalGroup(
            sousPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        sousPanelLayout.setVerticalGroup(
            sousPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sousPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(sousPanel);

        jScrollPane2.setBorder(null);

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setPreferredSize(new java.awt.Dimension(0, 150));

        panelRed.setBackground(new java.awt.Color(0, 0, 0));
        panelRed.setPreferredSize(new java.awt.Dimension(122, 110));
        panelRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelRedMouseClicked(evt);
            }
        });

        labelRed.setBackground(new java.awt.Color(0, 0, 0));
        labelRed.setText("jLabel3");
        labelRed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRedMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRedLayout = new javax.swing.GroupLayout(panelRed);
        panelRed.setLayout(panelRedLayout);
        panelRedLayout.setHorizontalGroup(
            panelRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRedLayout.createSequentialGroup()
                .addComponent(labelRed, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelRedLayout.setVerticalGroup(
            panelRedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelRed, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelGreen.setBackground(new java.awt.Color(0, 0, 0));
        panelGreen.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panelGreen.setPreferredSize(new java.awt.Dimension(122, 110));
        panelGreen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                panelGreenKeyPressed(evt);
            }
        });

        labelGreen.setBackground(new java.awt.Color(0, 0, 0));
        labelGreen.setText("jLabel4");
        labelGreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelGreenMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelGreenLayout = new javax.swing.GroupLayout(panelGreen);
        panelGreen.setLayout(panelGreenLayout);
        panelGreenLayout.setHorizontalGroup(
            panelGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
            .addGroup(panelGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(labelGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
        );
        panelGreenLayout.setVerticalGroup(
            panelGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 110, Short.MAX_VALUE)
            .addGroup(panelGreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(labelGreen, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
        );

        panelBlue.setBackground(new java.awt.Color(0, 0, 0));
        panelBlue.setPreferredSize(new java.awt.Dimension(122, 110));
        panelBlue.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                panelBlueKeyPressed(evt);
            }
        });

        labelBlue.setBackground(new java.awt.Color(0, 0, 0));
        labelBlue.setText("jLabel5");
        labelBlue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelBlueMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelBlueLayout = new javax.swing.GroupLayout(panelBlue);
        panelBlue.setLayout(panelBlueLayout);
        panelBlueLayout.setHorizontalGroup(
            panelBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 143, Short.MAX_VALUE)
            .addGroup(panelBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelBlueLayout.createSequentialGroup()
                    .addComponent(labelBlue, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 10, Short.MAX_VALUE)))
        );
        panelBlueLayout.setVerticalGroup(
            panelBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 110, Short.MAX_VALUE)
            .addGroup(panelBlueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(labelBlue, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
        );

        panelSepia.setBackground(new java.awt.Color(0, 0, 0));
        panelSepia.setPreferredSize(new java.awt.Dimension(122, 110));
        panelSepia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelSepiaMouseClicked(evt);
            }
        });

        labelSepia.setBackground(new java.awt.Color(0, 0, 0));
        labelSepia.setText("jLabel6");
        labelSepia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelSepiaMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelSepiaLayout = new javax.swing.GroupLayout(panelSepia);
        panelSepia.setLayout(panelSepiaLayout);
        panelSepiaLayout.setHorizontalGroup(
            panelSepiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
            .addGroup(panelSepiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelSepiaLayout.createSequentialGroup()
                    .addComponent(labelSepia, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        panelSepiaLayout.setVerticalGroup(
            panelSepiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 111, Short.MAX_VALUE)
            .addGroup(panelSepiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(labelSepia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
        );

        panelGray.setBackground(new java.awt.Color(0, 0, 0));
        panelGray.setPreferredSize(new java.awt.Dimension(122, 110));

        labelGray.setBackground(new java.awt.Color(0, 0, 0));
        labelGray.setText("jLabel3");
        labelGray.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelGrayMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelGrayLayout = new javax.swing.GroupLayout(panelGray);
        panelGray.setLayout(panelGrayLayout);
        panelGrayLayout.setHorizontalGroup(
            panelGrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGrayLayout.createSequentialGroup()
                .addComponent(labelGray, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(98, Short.MAX_VALUE))
        );
        panelGrayLayout.setVerticalGroup(
            panelGrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelGray, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 52, Short.MAX_VALUE)
                .addComponent(panelGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelBlue, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSepia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(panelRed, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelGray, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelBlue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(panelGray, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelRed, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(panelSepia, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addContainerGap())
        );

        jScrollPane2.setViewportView(jPanel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 815, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 819, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(358, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                    .addGap(175, 175, 175)))
        );

        saturationSlide.setMinimum(1);
        saturationSlide.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                saturationSlideStateChanged(evt);
            }
        });

        luminositeSlide.setMaximum(200);
        luminositeSlide.setValue(100);
        luminositeSlide.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                luminositeSlideStateChanged(evt);
            }
        });

        contrasteSlide.setMaximum(200);
        contrasteSlide.setMinorTickSpacing(1);
        contrasteSlide.setValue(100);
        contrasteSlide.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contrasteSlideStateChanged(evt);
            }
        });

        jLabel3.setText("Luminosité");

        jLabel4.setText("Contraste");

        jLabel5.setText("Saturation");

        rgbChartPanel.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout rgbChartPanelLayout = new javax.swing.GroupLayout(rgbChartPanel);
        rgbChartPanel.setLayout(rgbChartPanelLayout);
        rgbChartPanelLayout.setHorizontalGroup(
            rgbChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        rgbChartPanelLayout.setVerticalGroup(
            rgbChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 321, Short.MAX_VALUE)
        );

        jMenuBar1.setBackground(new java.awt.Color(51, 51, 51));

        openFileButton.setText("Fichier");
        openFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileButtonActionPerformed(evt);
            }
        });

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Ouvrir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        openFileButton.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Enregistrer");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        openFileButton.add(jMenuItem2);
        openFileButton.add(jSeparator1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem3.setText("Sortir");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        openFileButton.add(jMenuItem3);

        jMenuBar1.add(openFileButton);

        jMenu2.setText("Edition");

        jMenu7.setText("Contour");

        jMenuItem37.setText("Contour Vertical");
        jMenuItem37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem37ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem37);

        jMenuItem38.setText("Contour Horizontal");
        jMenuItem38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem38ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem38);

        jMenu2.add(jMenu7);

        jMenu1.setText("Transformation");

        jMenuItem6.setText("Homotetie");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem5.setText("Rotation");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem29.setText("Cisaillement");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem29);

        jMenuItem7.setText("Inclinaison");
        jMenu1.add(jMenuItem7);
        jMenu1.add(jSeparator2);

        jMenuItem10.setText("Rotation 180");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        jMenuItem11.setText("Rotation 90 horaire");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem12.setText("Rotation 90 antihoraire");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);
        jMenu1.add(jSeparator3);

        jMenuItem9.setText("Symétrie horizontal");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem9);

        jMenuItem13.setText("Symétrie vertical");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem13);

        jMenu2.add(jMenu1);

        cadreMenuItem.setText("Cadre");
        cadreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cadreMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(cadreMenuItem);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Image");

        jMenu4.setText("Mode");

        jMenuItem14.setText("Niveaux de gris");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem14);

        jMenuItem15.setText("RGB");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem15);

        jMenu3.add(jMenu4);

        jMenu5.setText("Réglages");

        jMenuItem16.setText("Luminosité");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem16);

        jMenuItem17.setText("Contraste");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem17);
        jMenu5.add(jSeparator4);

        jMenuItem18.setText("Vibrance");
        jMenu5.add(jMenuItem18);

        jMenuItem19.setText("Teinte");
        jMenu5.add(jMenuItem19);

        jMenuItem20.setText("Saturation");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem20);

        jMenuItem26.setText("Noir et blanc");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem26);
        jMenu5.add(jSeparator5);

        jMenuItem21.setText("Seuil");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem21);

        jMenuItem27.setText("Négatif");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem27);

        jMenuItem28.setText("Inversion");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem28);

        jMenuItem34.setText("Opacité");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem34);

        jMenuItem4.setText("Egaliser");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem4);

        jMenu3.add(jMenu5);
        jMenu3.add(jSeparator6);

        jMenuItem22.setText("Tonalité automatique");
        jMenu3.add(jMenuItem22);

        jMenuItem23.setText("Couleur automatique");
        jMenu3.add(jMenuItem23);

        jMenuItem24.setText("Contraste automatique");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem24);

        jMenuBar1.add(jMenu3);

        jMenu6.setText("Filtre");

        jMenuItem25.setText("Flou");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem25);

        jMenuItem30.setText("Gaussien");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem30);

        jMenuItem31.setText("Enhancement");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem31);

        jMenuItem32.setText("Laplacien");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem32);

        jMenuItem33.setText("Moyenneur");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem33);

        jMenuItem35.setText("Gradient oblique");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem35);

        jMenuItem36.setText("Texture");
        jMenuItem36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem36ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem36);

        specifiedColeurFilterButton.setText("Coleur");
        specifiedColeurFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specifiedColeurFilterButtonActionPerformed(evt);
            }
        });
        jMenu6.add(specifiedColeurFilterButton);

        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);
        jMenuBar1.getAccessibleContext().setAccessibleName("");
        jMenuBar1.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 815, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rgbChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(saturationSlide, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(luminositeSlide, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(contrasteSlide, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(0, 200, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rgbChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(luminositeSlide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(contrasteSlide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saturationSlide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        String fileName = file.getAbsolutePath();
        System.out.println("fichier selectionner "+ fileName);
        activeImage = loadFile(fileName);
        initialImage = loadFile(fileName);
        historiqueTraitement.clear();
        BufferedImage bufferedImage =(BufferedImage)activeImage ;
        
        addHistorique(bufferedImage);
        afficherImageInformation(bufferedImage.getHeight(),bufferedImage.getWidth(), fileName);
        indexHistorique  = 1;
        repaintPanelImage(bufferedImage);
        
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
             System.exit(0);
// TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void openFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileButtonActionPerformed
       
        // TODO add your handling code here:
    }//GEN-LAST:event_openFileButtonActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        JFileChooser saveFileChooser = new JFileChooser();
        BufferedImage buffered = (BufferedImage) activeImage;
        saveFileChooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));
        int returnValue = saveFileChooser.showSaveDialog(this);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            try {
                ImageIO.write(buffered,"png", saveFileChooser.getSelectedFile());
                System.out.println("Image file successfully saved!!");
            } catch (IOException ex) {
                Logger.getLogger(ImagView.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Filed to save image!!");
            }
        }else{
            System.out.println("not file chosen!");
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        try{
            BufferedImage img = (BufferedImage)activeImage;

            int[][] pixel = new int [img.getWidth()][img.getHeight()];
            int x,y,g;

            for(int i = 0; i < img.getWidth(); i++){
                for(int j = 0; j < img.getHeight(); j++){
                    
                    Color pixelcolor = new Color(img.getRGB(i, j));
                    int r = pixelcolor.getRed();
                    int gb = pixelcolor.getGreen();
                    int b = pixelcolor.getBlue();
                    int hy = (r + gb + b) / 3;
                    int rgb = new Color(hy,hy,hy).getRGB();
                    img.setRGB(i, j, rgb);
                    
                }
            }
            repaintPanelImage(img);
            addHistorique(img);
        }catch(Exception e){
            System.err.println("erreur ->" + e.getMessage());
        }
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
       rotationAngle(180);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
       rotationAngle(90);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        if((indexHistorique >= 0) && (historiqueTraitement.size() > 0)){
            BufferedImage buffered = (BufferedImage) activeImage;
            buffered = historiqueTraitement.get(indexHistorique);
            indexHistorique  = indexHistorique -1;
            repaintPanelImage(buffered);
        }
        
    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
        if(!historiqueTraitement.isEmpty() && indexHistorique < historiqueTraitement.size() -1){
            BufferedImage buffered = (BufferedImage) activeImage;
            buffered = historiqueTraitement.get(indexHistorique +1);
            indexHistorique  = indexHistorique +1;
            repaintPanelImage(buffered);
        }
    }//GEN-LAST:event_redoButtonActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        BufferedImage img = (BufferedImage) initialImage;
        repaintPanelImage(img);
        addHistorique(img);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        BufferedImage buffered = (BufferedImage) activeImage;
        BufferedImage result = new BufferedImage(buffered.getWidth(), buffered.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphic = result.createGraphics();
        graphic.drawImage(buffered, 0,0, Color.WHITE, null);
        graphic.dispose();
        repaintPanelImage(result);
        addHistorique(result);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        Transformation transformation = new Transformation();
        BufferedImage buffered = (BufferedImage) transformation.negatif(activeImage);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        Transformation transformation = new Transformation();
        Image image = transformation.inversion(activeImage);
        BufferedImage buffered = (BufferedImage) image;
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
       rotationAngle(-90);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        afficheControle(true,"rotation", 1);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void appliqueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appliqueButtonActionPerformed
        if("rotation".equals(activeAction)){
            rotationAngle(Integer.parseInt(firstText.getText()));
        }else if("homothetie".equals(activeAction)){
            Transformation transformation = new Transformation();
            Image homoImage = transformation.homothetie(activeImage,firstText.getText(),secondText.getText());
            BufferedImage buffered = (BufferedImage) homoImage;
            repaintPanelImage(buffered);
            addHistorique(buffered);
            
        System.out.println("L "+String.valueOf(buffered.getWidth()));
        System.out.println("H "+String.valueOf(buffered.getHeight()));
        }else if("cisaillement".equals(activeAction)){
            Transformation transformation = new Transformation();
            Image homoImage = transformation.cisaillement(activeImage,firstText.getText(),secondText.getText());
            BufferedImage buffered = (BufferedImage) homoImage;
            repaintPanelImage(buffered);
            addHistorique(buffered);
        }else if("cadre".equals(activeAction)){
            Color color = Color.BLACK;
            color = JColorChooser.showDialog(this,"Choisir une couleur de la cadre",color);
            int borderLeft =Integer.parseInt(firstText.getText());
            int borderTop = Integer.parseInt(firstText.getText());
            BufferedImage bufferedImage = (BufferedImage)activeImage;
            int borderedImageWidth = bufferedImage.getWidth() + ( borderLeft * 2);
            int borderedImageHeight = bufferedImage.getHeight()  + (borderTop * 2);
            BufferedImage img = new BufferedImage(borderedImageWidth, borderedImageHeight, BufferedImage.TYPE_3BYTE_BGR);
            img.createGraphics();
            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setColor(color);
            g.fillRect(0, 0, borderedImageWidth, borderedImageHeight);
            g.drawImage(bufferedImage, borderLeft, borderTop,bufferedImage.getWidth() + borderLeft, bufferedImage.getHeight() + borderTop, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), Color.YELLOW, null);
            repaintPanelImage(img);
            addHistorique(img);
        }else if("seuil".equals(activeAction)){
            Transformation transformation = new Transformation();
            Image seuilImage = transformation.seuillage(activeImage,Integer.parseInt(firstText.getText()));
            BufferedImage buffered = (BufferedImage) seuilImage;
            repaintPanelImage(buffered);
            addHistorique(buffered);
        }
    }//GEN-LAST:event_appliqueButtonActionPerformed

    private void annulerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annulerButtonActionPerformed
        afficheControle(false,"", 2);
    }//GEN-LAST:event_annulerButtonActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
       DinamiqueFilter dinamiqueFilter  = new DinamiqueFilter();
       BufferedImage imageFlou = dinamiqueFilter.getImageByFilter(activeImage);
        repaintPanelImage(imageFlou);
        addHistorique(imageFlou);
        
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        afficheControle(true,"homothetie", 2);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        afficheControle(true,"cisaillement", 2);
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        Transformation transformation = new Transformation();
            Image homoImage = transformation.symetrieX(activeImage);
            BufferedImage buffered = (BufferedImage) homoImage;
            repaintPanelImage(buffered);
            addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        Transformation transformation = new Transformation();
        Image homoImage = transformation.symetrieY(activeImage);
        BufferedImage buffered = (BufferedImage) homoImage;
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.luminosite(activeImage,10,"luminosite");
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f,
        };
        
        float[] masqueContraste = 
        {
            1f, -3f, 1f,
            -3f, 9f, -3f,
            1f, -3f, 1f,
        };

        Kernel masque = new Kernel(3,3,masqueContraste);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.opacity(activeImage);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.luminosite(activeImage,10,"luminosite");
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            1/16f, 1/8f, 1/16f,
            1/8f, 1/4f, 1/8f,
            1/16f, 1/8f, 1/16f,
        };
        
        Kernel masque = new Kernel(3,3,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            1f, 1f, 1f,
            1f, -8f, 1f,
            1f, 1f, 1f,
        };
        
        
        Kernel masque = new Kernel(3,3,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed
        BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            -1f, -1f, 0f,
            -1f, 0f, 1f,
            0f, 1f, 1f,
        };
        
        Kernel masque = new Kernel(3,3,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f,
        };
        
        Kernel masque = new Kernel(3,3,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem31ActionPerformed

    private void jMenuItem36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem36ActionPerformed
        BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            1f, -2f, 1f,
            -2f, 5f, -2f,
            1f, -2f, 1f,
        };
        
        Kernel masque = new Kernel(3,3,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem36ActionPerformed

    private void jMenuItem38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem38ActionPerformed
        BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            -1f, -1f, -1f,
            0f, 0f, 0f,
            1f, 1f, 1f,
        };
        
        Kernel masque = new Kernel(3,3,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem38ActionPerformed

    private void jMenuItem37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem37ActionPerformed
       BufferedImage buffered = (BufferedImage)activeImage;
        BufferedImage imageFlou = new BufferedImage(buffered.getWidth(), buffered.getHeight(), buffered.getType());
        float[] masqueFlou = 
        {
            -1f, 0f, 1f,
            -1f, 0f, 1f,
            -1f, 0f, 1f,
        };
        
        Kernel masque = new Kernel(3,3,masqueFlou);
        ConvolveOp operation = new ConvolveOp(masque);
        operation.filter(buffered, imageFlou);
        repaintPanelImage(imageFlou);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem37ActionPerformed

    private void panelGreenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panelGreenKeyPressed
        ColorFilter colorFilter = new ColorFilter();
       BufferedImage buffered = (BufferedImage)colorFilter.filterGreen(activeImage);
       repaintPanelImage(buffered);
       addHistorique(buffered);
    }//GEN-LAST:event_panelGreenKeyPressed

    private void panelBlueKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panelBlueKeyPressed
       ColorFilter colorFilter = new ColorFilter();
       BufferedImage buffered = (BufferedImage)colorFilter.filterBlue(activeImage);
       repaintPanelImage(buffered);
       addHistorique(buffered);
    }//GEN-LAST:event_panelBlueKeyPressed

    private void panelSepiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelSepiaMouseClicked
       ColorFilter colorFilter = new ColorFilter();
       BufferedImage buffered = (BufferedImage)colorFilter.filterSepia(activeImage,0);
       repaintPanelImage(buffered);
       addHistorique(buffered);
    }//GEN-LAST:event_panelSepiaMouseClicked

    private void panelRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRedMouseClicked
        ColorFilter colorFilter = new ColorFilter();
       BufferedImage buffered = (BufferedImage)colorFilter.filterRed(activeImage);
       repaintPanelImage(buffered);
       addHistorique(buffered);
    }//GEN-LAST:event_panelRedMouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        ImageMatrice imageMatrice = new ImageMatrice();
        int[][] matriceRed = imageMatrice.getRedMatrice(activeImage);
        int[][] matriceGreen = imageMatrice.getGreenMatrice(activeImage);
        int[][] matriceBlue = imageMatrice.getBlueMatrice(activeImage);
        int width = matriceRed.length;
        int height = matriceRed[0].length;
        int[][] niveauRed = EgalisationNiveauGris.matriceDesire(matriceRed);
        int[][] niveauGreen =EgalisationNiveauGris.matriceDesire(matriceGreen);
        int[][] niveauBlue = EgalisationNiveauGris.matriceDesire(matriceBlue);
        BufferedImage buffered = (BufferedImage) imageMatrice.getImageByRGB(activeImage,niveauRed,niveauGreen,niveauBlue);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void labelGreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelGreenMouseClicked
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.filterGreen(activeImage);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_labelGreenMouseClicked

    private void labelBlueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelBlueMouseClicked
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.filterBlue(activeImage);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_labelBlueMouseClicked

    private void labelSepiaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelSepiaMouseClicked
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.filterSepia(activeImage,0);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_labelSepiaMouseClicked

    private void labelRedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRedMouseClicked
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.filterRed(activeImage);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_labelRedMouseClicked

    private void labelGrayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelGrayMouseClicked
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)filterGray(activeImage);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_labelGrayMouseClicked

    private void specifiedColeurFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specifiedColeurFilterButtonActionPerformed
        ColorFilter colorFilter = new ColorFilter();
        Color color = Color.BLACK;
        color = JColorChooser.showDialog(this,"Choisir une couleur",color);
        BufferedImage buffered = (BufferedImage)colorFilter.specifiedColorFileter(activeImage,color);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_specifiedColeurFilterButtonActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.luminosite(activeImage,10,"saturation");
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void saturationSlideStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_saturationSlideStateChanged
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.luminosite(activeImage,saturationSlide.getValue(),"saturation");
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_saturationSlideStateChanged

    private void luminositeSlideStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_luminositeSlideStateChanged
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.nouveauLuminosite(activeImage,luminositeSlide.getValue());
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_luminositeSlideStateChanged

    private void cadreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cadreMenuItemActionPerformed
        afficheControle(true,"cadre", 1);
    }//GEN-LAST:event_cadreMenuItemActionPerformed

    private void contrasteSlideStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contrasteSlideStateChanged
        ColorFilter colorFilter = new ColorFilter();
        BufferedImage buffered = (BufferedImage)colorFilter.contraste(activeImage,contrasteSlide.getValue());
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_contrasteSlideStateChanged

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        afficheControle(true,"seuil", 1);
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void zoomupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomupButtonActionPerformed
        Zoom zoom = new Zoom();
        BufferedImage buffered = (BufferedImage)zoom.setZoom(activeImage,2);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_zoomupButtonActionPerformed

    private void zoomdownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomdownButtonActionPerformed
        Zoom zoom = new Zoom();
        BufferedImage buffered = (BufferedImage)zoom.setZoom(activeImage,0.5);
        repaintPanelImage(buffered);
        addHistorique(buffered);
    }//GEN-LAST:event_zoomdownButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ImagView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ImagView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ImagView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ImagView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ImagView().setVisible(true);
                
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton annulerButton;
    private javax.swing.JButton appliqueButton;
    private javax.swing.JMenuItem cadreMenuItem;
    private javax.swing.JSlider contrasteSlide;
    private javax.swing.JTextField firstText;
    private javax.swing.JLabel hauteurImage;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelBlue;
    private javax.swing.JLabel labelGray;
    private javax.swing.JLabel labelGreen;
    private javax.swing.JLabel labelImage;
    private javax.swing.JLabel labelRed;
    private javax.swing.JLabel labelSepia;
    private javax.swing.JLabel largeurImage;
    private javax.swing.JSlider luminositeSlide;
    private javax.swing.JLabel nomFichier;
    private javax.swing.JMenu openFileButton;
    private javax.swing.JPanel panelBlue;
    private javax.swing.JPanel panelGray;
    private javax.swing.JPanel panelGreen;
    private javax.swing.JPanel panelRed;
    private javax.swing.JPanel panelSepia;
    private javax.swing.JButton redoButton;
    private javax.swing.JPanel rgbChartPanel;
    private javax.swing.JSlider saturationSlide;
    private javax.swing.JTextField secondText;
    private javax.swing.JPanel sousPanel;
    private javax.swing.JMenuItem specifiedColeurFilterButton;
    private javax.swing.JButton undoButton;
    private javax.swing.JButton zoomdownButton;
    private javax.swing.JButton zoomupButton;
    // End of variables declaration//GEN-END:variables
}
