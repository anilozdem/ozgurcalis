import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.SocketException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.net.ftp.FTPClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Anıl
 */
public class OzgurCalis extends javax.swing.JFrame  {
    
    //upload edilecek dosyanın yolunu tutan değişken, uzak sunucu bağlantısına bu değişkendeki yol gönderilecek
    String secilenDosyaninYolu;
    
    //Timer için saniye tutucu değişken
    String secs;
    
    //getCurrentDirectory fonksiyonu ile alınan ve programın çalıştırıldığı klasörün yolunu içinde tutacak string değişkeni
    String canonicalPath;
    
    //FileChooser frame'ini daha sonra açmak için obje tanımlaması
    JFrame FileChooser;
    
    //sisteme giriş yapan kullanıcının türünü, ismini, soyismini ve epostasını tutmak için değişken
    int girisYapanKullaniciTuru;
    String girisYapanKullaniciIsmi,
           girisYapanKullaniciSoyismi,
           girisYapanKullaniciEpostasi;
    
    //gişir sırasında üst menü iconlarını giriş yapılmıyorsa pasif tutmak için değişken
    int ustMenuSecimi;
    
    //getprocesses fonsiyonunda görev başarılı veya başarısız olduyu kontrol etmek için flag
    boolean flag = false; //şu an başarısız
    
    //hangi görevde olduğumuzu anlamak için görev sırasını tutmak için değişken
    int gecerliGorev;

    /* gorev geri sayımı için gerekli değişkenler */
    static int interval;
    static Timer timer;
    
    //görev adlarını alırken istenildiğinde istenilen görevin adını tutmak için değişken
    String gorevAdiTutucu;
    
    //görev exe adlarını alırken istenildiğinde istenilen görevin exe adını tutmak için değişken
    String gorevExeAdiTutucu;
    
    //uye girisi yapacak kullanıcıların türleri
    String uyeTipleri[] = {"Kişisel", "Öğrenci", "Eğitmen"};
 
    //private Tasks nTask = new Tasks();
//    int taskPosition = 1;
//    int menuSelection = 0;
//    boolean flag = false;
//    boolean userStatus = false;
    
    //ileri, geri butonları için enable, disable iconları
    ImageIcon nextDisabled;
    ImageIcon nextEnabled;
    ImageIcon previousDisabled; 
    ImageIcon previousEnabled;
    
    //ülke iconları ve listelenecek ülke iconu için currentCountry icon değişkeni
    ImageIcon turkiye; 
    ImageIcon ispanya; 
    ImageIcon jamaika; 
    ImageIcon currentCountry;
    
    /*XML dosyalarından gelen değişkenler */
    //gorevler.XML değişkenleri
    String gorevId, gorevAdi, gorevAciklamasi, gorevExeAdi, gorevIlgiliGorevler;
    
    //uyeler.XML değişkenleri
    String uyelerIsim, uyelerSoyisim, uyelerSifre, uyelerEposta, uyelerKisiselSeviye;
    
    //siniflar.XML değişkenleri
    String sinifAdi, sinifSehri, sinifUlkesi, sinifKullaniciSayisi, sinifKapasitesi, sinifEgitmeni, sinifKayitliUyeler[], sinifSeviyesi, sinifKayitliUyelerTemp;
    
    //uploadlar.XML değişkenleri
    String uploadEdenOgrenci, uploadEdilenSinif, uploadEdilenGorevNosu, uploadEdilenDosyaninAdi;
    
    //beniHatirla.XML değişkenleri
    String beniHatirlaEposta, beniHatirlaSifre, beniHatirlaUyeTipi;

    //XML dosyalarından gelen değişkenlerin array listesi (çıkış yapılınca hepsine null atamak için
    String XML[] = {gorevId,gorevAdi, gorevAciklamasi, gorevExeAdi, gorevIlgiliGorevler, uyelerIsim, uyelerSoyisim, uyelerSifre, uyelerEposta
            ,uyelerKisiselSeviye, sinifAdi, sinifSehri, sinifUlkesi, sinifKullaniciSayisi, sinifKapasitesi
            , sinifEgitmeni, sinifKayitliUyelerTemp, sinifSeviyesi, uploadEdenOgrenci, uploadEdilenSinif, uploadEdilenGorevNosu, uploadEdilenDosyaninAdi
            , beniHatirlaEposta, beniHatirlaSifre, beniHatirlaUyeTipi};
    
    //XML dosyalarından gelecek verileri geçici olarak tutacak Node'lar
    NodeList nodeListGorevler, nodeListUyeler, nodeListSiniflar, nodeListUploadlar, nodelistBeniHatirla;
    
    //XML dosyalarından veri almak için gerekli object tanımlamaları
    Document doc;
    DocumentBuilderFactory dbf;
    DocumentBuilder db;
   
    /* Constructor */
    /* Creates new form ozgurcalis */
    public OzgurCalis() {      
        initComponents();
        
        jLabel45.setText("Platform: " + System.getProperty("os.name"));
        jLabel45.setVisible(true);
        
        //Timer için saniye tutucu değişkene görev süresi ataması
        secs = "6";
        
        //getCurrentDirectory fonksiyonu ile alınan ve programın çalıştırıldığı klasörün yolunu içinde tutacak string değişkenine yolu atamak için fonksiyonu çağırıyoruz
        getCurrentDirectory();
        
        //FileChooser frame'ini daha sonra açmak için obje tanımlamasından sonra obje yaratılması
        FileChooser = new FileChooser();
        
        //gişir sırasında üst menü iconlarını giriş yapılmıyorsa pasif tutmak için değişkene 0 atanır
        ustMenuSecimi = 0;
        
        //geçerli görevi birinci göreve eşitler
        gecerliGorev = 1;
        
        //bütün node'ları sıfırlar
        nodeListGorevler = null; 
        nodeListUyeler = null;
        nodeListSiniflar = null;
        nodeListUploadlar = null;
        nodelistBeniHatirla = null;
        
        /* butun XML dosyalarından gelen degiskenlerin içini boşaltır */
        XML = null;
        sinifKayitliUyeler = null;
        gorevAdiTutucu = null;
        gorevExeAdiTutucu = null;
        
        /* bütün gorevlerin, uyelerin, siniflarin ve uploadların listesini alir ilgili node'ların içlerine listeler */    
        getGorevlerFromXML(2,gecerliGorev);
        getGorevlerFromXML(0,99);
        getBeniHatirlaFromXML();
        getSiniflarFromXML(99);
        getUyelerFromXML(0);
        getUploadlarFromXML();
     
        //program içinde kullanılan bütün butonların arkaplanlarını saydamlaştırır
        setTransparentToButtons();
     
        /* program içinde kullanılan bütün alt panelleri (18 tane) saydamlaştırır (fonksiyon görünür yapacağı panelin
         * String array içindeki pozisyonunu beklediği için olmayan bir pozisyon olarak 99 yollandı ki bütün paneller saydam olsun)*/
        setSubPanelToVisible(99);
        
        //programın ekranın tam ortasında başlaması için gerekli kod
        this.setLocationRelativeTo(null);
        
        /* ilkEkran dışındaki bütün 12 ana paneli program başlarken görünmez hale getirir
         * Ana panellerin String array içindeki pozisyonları aşağıda listelenmiştir (toplam 13 tane)       
         * uyeEkrani = 0, uyeGirisi = 1, anaEkran = 2, destekKutuphanesi = 3, siniflar = 4,
         * sinifKontrol = 5, ilkEkran = 6, gorevler = 7, uyeOl = 8 
         * anaEkranKisisel = 9, uyeEkraniKisisel = 10, anaEkranEgitmen = 11, egitimlerEdit = 12*/
        anaPanellerArasiGecis(6);
        
        //daha sonra gorevler panelinde yer alan ileri/geri butonlarına atanacak iconlar
        this.nextDisabled = new ImageIcon(getClass().getResource("ileriDisabled.png"));
        this.nextEnabled = new ImageIcon(getClass().getResource("ileri.png"));    
        this.previousDisabled = new ImageIcon(getClass().getResource("geriDisabled.png")); 
        this.previousEnabled = new ImageIcon(getClass().getResource("geri.png"));
        
        //daha sonra sınıflar paneli içinde yer alan jTable'da ülke bayrakları kısmına atanacak ülke iconları
        this.turkiye = new ImageIcon(getClass().getResource("tr.png")); 
        this.ispanya = new ImageIcon(getClass().getResource("es.png")); 
        this.jamaika = new ImageIcon(getClass().getResource("jm.png")); 
        
        //gorevler panelindeki ileri butonunu disable yapar ve iconunu disable icon ile değiştirir
        jButton5.setEnabled(false);
        jButton5.setIcon(nextDisabled);
        
        //ustMenu panelinde yer alan Hoşgeldiniz text'ini, Kullanıcı ismi ve Çıkış text'ini taşıyan jLabel'lar açılışta gizlenir (giriş yapıldıktan sonra görünür hale getirilir)
        jLabel21.setVisible(false);
        jLabel22.setText("");
        jLabel31.setVisible(false);

        //programın kendi icon'unu atar
        setIcon();   
    }
    
    //programın kendi iconunun yolu burda belirtilmiştir
    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ozgurcalis.png")));
    }
    
    //programın çalıştırıldığı klasörün yolunu alan program
    private void getCurrentDirectory() {
        try {
            File file = new File(".");  
            File[] files = file.listFiles();
            canonicalPath = file.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    //butonlara tıklandığında kullanılacak click sesi fonksiyonu
    public static void buttonClickSound() {
        try {          
            String soundName = "buttonClick.wav";    
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();                                  
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "File not found!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "IOException problem!");
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
    }

    //hangi panel ne zaman gösterilecekse parametre olarak alınır, geri kalan bütün ana paneller gizlenir
    private void anaPanellerArasiGecis(int truePanelNumber){
        JPanel allPanels[] = {uyeEkrani, uyeGirisi, anaEkran, destekKutuphanesi, siniflar, sinifKontrol, ilkEkran, gorevler, uyeOl
        , anaEkranKisisel, uyeEkraniKisisel, anaEkranEgitmen, egitimlerEdit};
        for(int i=0; i < allPanels.length; i++) {
            if(i == truePanelNumber) {
                allPanels[i].setVisible(true);
            } else {
                allPanels[i].setVisible(false);
            }           
        }
    }
    
    //prrogram başlangıcında kullanılan bütün istenilen butonların arkaplanlarını saydamlarştıran fonksiyon
    private void setTransparentToButtons(){
        JButton butonlar[] = {jButton1, jButton2, jButton3, jButton4, jButton5, jButton6, jButton7, jButton8
        , jButton9, jButton10, jButton12, jButton13, jButton14, jButton15, jButton16, ogrenci, egitmen, kisiselKullanim, yeniHesapOlustur, anasayfa, kisisel, jButton25
        , jButton26, jButton27, jButton28, jButton29, jButton30, jButton31, jButton32, jButton33
        , jButton34, jButton44, jButton45, jButton46, jButton47, jButton52, jButton53, jButton54, jButton55, jButton56, jButton57, jButton58, jButton59
        , jButton60, jButton61, jButton62}; // visible, invisible özelliğini ayarlamak için bütün butonların listesi
        for(int i=0; i < butonlar.length; i++) {
                butonlar[i].setOpaque(false);
                butonlar[i].setContentAreaFilled(false);
                butonlar[i].setBorderPainted(false);     
        }
    }
    
    //hangi alt panel ne zaman gösterilecekse parametre olarak alınır, geri kalan bütün alt paneller gizlenir
    private void setSubPanelToVisible(int trueSubPanelNumber){
        JPanel subPanels[]= {jPanel1, jPanel2, jPanel3, jPanel4, jPanel5, jPanel6, jPanel7, jPanel8
        , jPanel9, jPanel10, jPanel11, jPanel12, jPanel13, jPanel14, jPanel15, jPanel16, jPanel17, jPanel18};
        for(int i=0; i < subPanels.length; i++) {
            if(i == trueSubPanelNumber) {
                subPanels[i].setVisible(true);
            } else {
                subPanels[i].setVisible(false);
            }          
        }
    }
    
        //getProcesses() fuction to check taskManager and running processes
    public void getProcesses() {
        getGorevlerFromXML(2,gecerliGorev);
        try {
            String line;
            Process prcs = null;
            
            if(System.getProperty("os.name").contains("Windows")) {
               //WINDOWS görev yöneticisi işlemlerini kontrol eden kod
                prcs = Runtime.getRuntime().exec
                (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
            }
            else if(System.getProperty("os.name").contains("Ubuntu")) {
                //UBUNTU sistem gözlemcisi işlemlerini kontrol eden kod
                prcs = Runtime.getRuntime().exec("ps -e");
            }
            

            BufferedReader input;
            input = new BufferedReader(new InputStreamReader(prcs.getInputStream()));

            while ((line = input.readLine()) != null) {            
                if(line.toLowerCase().contains(gorevExeAdiTutucu)) {
                    JOptionPane.showMessageDialog(null, "Tebrikler! Bir sonraki aşamaya geçebilirsiniz");                  
                    jButton5.setEnabled(true);
                    jButton5.setIcon(nextEnabled);
                    jButton6.setEnabled(false);
                    jButton7.setEnabled(false);                                          
                    flag = true;//görev başarılıya çevirildi   
                    
                    //giriş yapmış olan kullanıcı kişisel ise, görev tamamlandıkça seviyesi artırılır
                    if(girisYapanKullaniciTuru == 0) {
                        
                    }
                }                        
            }
            if(flag  == false) {
                JOptionPane.showMessageDialog(null, "Üzgünüm, başarısız oldunuz.Tekrar deneyin!");
            } else {
                if(gecerliGorev == nodeListGorevler.getLength()) {
                JOptionPane.showMessageDialog(null, "Bütün aşamaları tamamladınız!");
                jButton6.setEnabled(false);
                jButton7.setEnabled(false);
                jButton5.setEnabled(false);
                jButton5.setIcon(nextDisabled);
                }
            }
            input.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "İşlem kontrolü hatası!");
        }
    }
    
    //görev için belirlenen sürenin geri sayımını başlatan fonksiyon
    private void startTimer() {       
        int delay = 1000;
        int period = 1000;
        timer = new Timer();
        interval = Integer.parseInt(secs);
    
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if(interval < 0) {
                    jLabel2.setText("Kalan Zaman: 0 Saniye");
                } else {
                    jLabel2.setText("Kalan Zaman: "+String.valueOf(setInterval())+" Saniye");
                    if(interval == 0) {
                        getProcesses();
                    }              
                }
            }
        }, delay, period);
    }
    private int setInterval() {
        if (interval == 1) {
            timer.cancel();
        }
        //Kullanıcı çıkış yaptığı için işlem bitirilir
        if (interval == -1) {
            timer.cancel();
            JOptionPane.showMessageDialog(null, "Çıkış yapıldığı için"+gorevAdiTutucu+"görev tamamlanamadı!");
        }
        //Kullanıcı görevi tamamlayıp tamamla butonuna bastığı için işlem tamamlanır
        if (interval == -2) {
            timer.cancel();          
        }
        return --interval;
    }
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        uyeOlRadioButton = new javax.swing.ButtonGroup();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        ilkEkran = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        ogrenci = new javax.swing.JButton();
        egitmen = new javax.swing.JButton();
        kisiselKullanim = new javax.swing.JButton();
        yeniHesapOlustur = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ustMenu = new javax.swing.JPanel();
        anasayfa = new javax.swing.JButton();
        kisisel = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        egitimlerEdit = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        anaEkranEgitmen = new javax.swing.JPanel();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        uyeEkraniKisisel = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        anaEkranKisisel = new javax.swing.JPanel();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        gorevler = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton60 = new javax.swing.JButton();
        jButton63 = new javax.swing.JButton();
        jTextField11 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jButton64 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton61 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jButton62 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        kalanZamanBg = new javax.swing.JLabel();
        gorevlerBg = new javax.swing.JLabel();
        uyeOl = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        sinifKontrol = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jButton43 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jButton42 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jButton41 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jButton40 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        siniflar = new javax.swing.JPanel();
        jButton52 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        uyeEkrani = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jButton39 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jButton38 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel30 = new javax.swing.JLabel();
        jButton36 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jButton37 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        destekKutuphanesi = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jButton53 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jButton54 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jButton55 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jButton56 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jButton57 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jButton58 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jButton59 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        anaEkran = new javax.swing.JPanel();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        uyeGirisi = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JPasswordField();
        jButton11 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jLayeredPane1.setBackground(new java.awt.Color(255, 255, 255));

        ilkEkran.setBackground(new java.awt.Color(255, 255, 255));
        ilkEkran.setLayout(null);

        jLabel36.setForeground(new java.awt.Color(51, 51, 51));
        jLabel36.setText("Kullanıcı grubunuzu seçerek devam ediniz;");
        ilkEkran.add(jLabel36);
        jLabel36.setBounds(60, 10, 400, 14);

        ogrenci.setBackground(new java.awt.Color(255, 255, 255));
        ogrenci.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        ogrenci.setBorder(null);
        ogrenci.setBorderPainted(false);
        ogrenci.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        ogrenci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ogrenciActionPerformed(evt);
            }
        });
        ilkEkran.add(ogrenci);
        ogrenci.setBounds(68, 40, 77, 77);

        egitmen.setBackground(new java.awt.Color(255, 255, 255));
        egitmen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        egitmen.setBorder(null);
        egitmen.setBorderPainted(false);
        egitmen.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        egitmen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                egitmenActionPerformed(evt);
            }
        });
        ilkEkran.add(egitmen);
        egitmen.setBounds(175, 40, 77, 77);

        kisiselKullanim.setBackground(new java.awt.Color(255, 255, 255));
        kisiselKullanim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        kisiselKullanim.setBorder(null);
        kisiselKullanim.setBorderPainted(false);
        kisiselKullanim.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        kisiselKullanim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kisiselKullanimActionPerformed(evt);
            }
        });
        ilkEkran.add(kisiselKullanim);
        kisiselKullanim.setBounds(420, 40, 77, 77);

        yeniHesapOlustur.setBackground(new java.awt.Color(255, 255, 255));
        yeniHesapOlustur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yeniUylikButon.png"))); // NOI18N
        yeniHesapOlustur.setBorder(null);
        yeniHesapOlustur.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/yeniUylikButonOver.png"))); // NOI18N
        yeniHesapOlustur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yeniHesapOlusturActionPerformed(evt);
            }
        });
        ilkEkran.add(yeniHesapOlustur);
        yeniHesapOlustur.setBounds(160, 170, 275, 37);

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel45.setText("Platform: ");
        ilkEkran.add(jLabel45);
        jLabel45.setBounds(450, 190, 150, 15);
        jLabel45.getAccessibleContext().setAccessibleName("");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ilkEkranBg.png"))); // NOI18N
        ilkEkran.add(jLabel5);
        jLabel5.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(ilkEkran);
        ilkEkran.setBounds(0, 0, 609, 215);

        ustMenu.setBackground(new java.awt.Color(255, 255, 255));
        ustMenu.setOpaque(false);
        ustMenu.setLayout(null);

        anasayfa.setBackground(new java.awt.Color(255, 255, 255));
        anasayfa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/menuHome.png"))); // NOI18N
        anasayfa.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/menuHomeOver.png"))); // NOI18N
        anasayfa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anasayfaActionPerformed(evt);
            }
        });
        ustMenu.add(anasayfa);
        anasayfa.setBounds(543, 0, 20, 21);

        kisisel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/menuPersonal.png"))); // NOI18N
        kisisel.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/menuPersonalOver.png"))); // NOI18N
        kisisel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kisiselActionPerformed(evt);
            }
        });
        ustMenu.add(kisisel);
        kisisel.setBounds(569, 0, 20, 21);

        jLabel21.setFont(new java.awt.Font("Ubuntu", 0, 11)); // NOI18N
        jLabel21.setText("Hoşgeldin,");
        ustMenu.add(jLabel21);
        jLabel21.setBounds(10, 3, 60, 15);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(153, 0, 0));
        ustMenu.add(jLabel22);
        jLabel22.setBounds(75, 3, 185, 15);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(153, 0, 0));
        ustMenu.add(jLabel23);
        jLabel23.setBounds(325, 3, 130, 15);
        ustMenu.add(jLabel24);
        jLabel24.setBounds(270, 3, 50, 15);

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(153, 0, 0));
        jLabel31.setText("Çıkış");
        ustMenu.add(jLabel31);
        jLabel31.setBounds(480, 3, 50, 15);

        jLayeredPane1.add(ustMenu);
        ustMenu.setBounds(0, 0, 609, 20);

        egitimlerEdit.setLayout(null);

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anaEkranBg.png"))); // NOI18N
        egitimlerEdit.add(jLabel35);
        jLabel35.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(egitimlerEdit);
        egitimlerEdit.setBounds(0, 0, 609, 215);

        anaEkranEgitmen.setLayout(null);

        jButton48.setText("EĞİTİMLER");
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });
        anaEkranEgitmen.add(jButton48);
        jButton48.setBounds(60, 40, 127, 126);

        jButton49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/destekKutuphanesi.png"))); // NOI18N
        jButton49.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/destekKutuphanesiOver.png"))); // NOI18N
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });
        anaEkranEgitmen.add(jButton49);
        jButton49.setBounds(440, 40, 127, 126);

        jButton50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/benimSayfam.png"))); // NOI18N
        jButton50.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/benimSayfamOver.png"))); // NOI18N
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });
        anaEkranEgitmen.add(jButton50);
        jButton50.setBounds(240, 180, 155, 29);

        jButton51.setText("SINIF YÖNETİM");
        anaEkranEgitmen.add(jButton51);
        jButton51.setBounds(250, 40, 127, 126);

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anaEkranBg.png"))); // NOI18N
        anaEkranEgitmen.add(jLabel33);
        jLabel33.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(anaEkranEgitmen);
        anaEkranEgitmen.setBounds(0, 0, 609, 215);

        uyeEkraniKisisel.setLayout(null);

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anaEkranBg.png"))); // NOI18N
        uyeEkraniKisisel.add(jLabel34);
        jLabel34.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(uyeEkraniKisisel);
        uyeEkraniKisisel.setBounds(0, 0, 609, 215);

        anaEkranKisisel.setLayout(null);

        jButton45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/egitimeBasla.png"))); // NOI18N
        jButton45.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/egitimebaslaOver.png"))); // NOI18N
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });
        anaEkranKisisel.add(jButton45);
        jButton45.setBounds(60, 40, 127, 126);

        jButton46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/destekKutuphanesi.png"))); // NOI18N
        jButton46.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/destekKutuphanesiOver.png"))); // NOI18N
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });
        anaEkranKisisel.add(jButton46);
        jButton46.setBounds(440, 40, 127, 126);

        jButton47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/benimSayfam.png"))); // NOI18N
        jButton47.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/benimSayfamOver.png"))); // NOI18N
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });
        anaEkranKisisel.add(jButton47);
        jButton47.setBounds(240, 180, 155, 29);

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anaEkranBg.png"))); // NOI18N
        anaEkranKisisel.add(jLabel32);
        jLabel32.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(anaEkranKisisel);
        anaEkranKisisel.setBounds(0, 0, 609, 215);

        gorevler.setBackground(new java.awt.Color(255, 255, 255));
        gorevler.setLayout(null);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(null);

        jButton60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton60.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton60.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton60ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton60);
        jButton60.setBounds(330, 0, 10, 10);

        jButton63.setText("ARA");
        jButton63.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton63ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton63);
        jButton63.setBounds(230, 50, 90, 31);

        jTextField11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField11MouseClicked(evt);
            }
        });
        jTextField11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField11ActionPerformed(evt);
            }
        });
        jPanel3.add(jTextField11);
        jTextField11.setBounds(20, 50, 200, 30);

        jLabel44.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel44.setText("<html>Lüften dosyanın yolunu belirtiniz</html>");
        jLabel44.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel44.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel44.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(jLabel44);
        jLabel44.setBounds(20, 25, 300, 20);

        jButton64.setText("GÖNDER");
        jButton64.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton64ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton64);
        jButton64.setBounds(120, 90, 100, 23);

        gorevler.add(jPanel3);
        jPanel3.setBounds(20, 40, 340, 130);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(null);

        jButton61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton61.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton61.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton61ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton61);
        jButton61.setBounds(330, 0, 10, 10);

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Benzer Görevler");
        jPanel2.add(jLabel43);
        jLabel43.setBounds(205, 0, 120, 20);

        jScrollPane4.setForeground(new java.awt.Color(51, 51, 51));
        jScrollPane4.setHorizontalScrollBar(null);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jScrollPane4.setViewportView(jTextArea2);

        jPanel2.add(jScrollPane4);
        jScrollPane4.setBounds(0, 20, 340, 110);

        gorevler.add(jPanel2);
        jPanel2.setBounds(20, 40, 340, 130);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(null);

        jScrollPane1.setForeground(new java.awt.Color(51, 51, 51));
        jScrollPane1.setHorizontalScrollBar(null);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(0, 20, 340, 110);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("İpucu");
        jPanel1.add(jLabel18);
        jLabel18.setBounds(205, 0, 120, 20);

        jButton62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton62.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton62.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton62ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton62);
        jButton62.setBounds(330, 0, 10, 10);

        gorevler.add(jPanel1);
        jPanel1.setBounds(20, 40, 340, 130);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lineEffect.png"))); // NOI18N
        gorevler.add(jLabel6);
        jLabel6.setBounds(370, 40, 3, 112);

        jLabel1.setText("GÖREV 1: ");
        gorevler.add(jLabel1);
        jLabel1.setBounds(22, 21, 59, 24);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ipucu.png"))); // NOI18N
        jButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/ipucuOver.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gorevler.add(jButton1);
        jButton1.setBounds(410, 30, 171, 35);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ilgiliGorevler.png"))); // NOI18N
        jButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/ilgiliGorevlerOver.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gorevler.add(jButton2);
        jButton2.setBounds(410, 80, 171, 35);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dosyaEkle.png"))); // NOI18N
        jButton3.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/dosyaEkleOver.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gorevler.add(jButton3);
        jButton3.setBounds(410, 130, 171, 35);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/geri.png"))); // NOI18N
        jButton4.setBorder(null);
        jButton4.setEnabled(false);
        jButton4.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/geriOver.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gorevler.add(jButton4);
        jButton4.setBounds(20, 180, 50, 20);

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ileri.png"))); // NOI18N
        jButton5.setBorder(null);
        jButton5.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/ileriOver.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        gorevler.add(jButton5);
        jButton5.setBounds(540, 180, 50, 20);

        jLabel2.setFont(new java.awt.Font("Berlin Sans FB", 0, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Kalan Zaman: 0 Saniye");
        gorevler.add(jLabel2);
        jLabel2.setBounds(210, 185, 190, 20);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/baslat.png"))); // NOI18N
        jButton6.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/baslatOver.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        gorevler.add(jButton6);
        jButton6.setBounds(110, 180, 94, 31);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tamamla.png"))); // NOI18N
        jButton7.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/tamamlaOver.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        gorevler.add(jButton7);
        jButton7.setBounds(405, 180, 94, 31);
        gorevler.add(jLabel3);
        jLabel3.setBounds(91, 21, 120, 24);

        kalanZamanBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kalanZamanBg.png"))); // NOI18N
        gorevler.add(kalanZamanBg);
        kalanZamanBg.setBounds(105, 175, 400, 40);

        gorevlerBg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gorevlerBg.png"))); // NOI18N
        gorevler.add(gorevlerBg);
        gorevlerBg.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(gorevler);
        gorevler.setBounds(0, 0, 609, 215);

        uyeOl.setLayout(null);

        jLabel12.setText("İsim:");
        uyeOl.add(jLabel12);
        jLabel12.setBounds(80, 40, 70, 27);

        jLabel13.setText("Soyisim:");
        uyeOl.add(jLabel13);
        jLabel13.setBounds(310, 40, 70, 27);

        jLabel14.setText("E-posta:");
        uyeOl.add(jLabel14);
        jLabel14.setBounds(80, 80, 70, 27);

        jLabel15.setText("Şifre:");
        uyeOl.add(jLabel15);
        jLabel15.setBounds(310, 80, 70, 27);

        jRadioButton2.setBackground(new java.awt.Color(255, 255, 255));
        uyeOlRadioButton.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Öğrenci");
        uyeOl.add(jRadioButton2);
        jRadioButton2.setBounds(270, 110, 90, 23);

        jRadioButton3.setBackground(new java.awt.Color(255, 255, 255));
        uyeOlRadioButton.add(jRadioButton3);
        jRadioButton3.setText("Eğitmen");
        uyeOl.add(jRadioButton3);
        jRadioButton3.setBounds(360, 110, 90, 23);

        jRadioButton4.setBackground(new java.awt.Color(255, 255, 255));
        uyeOlRadioButton.add(jRadioButton4);
        jRadioButton4.setText("Kişisel");
        uyeOl.add(jRadioButton4);
        jRadioButton4.setBounds(450, 110, 80, 23);

        jTextField4.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(54, 54, 54));
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField4.setToolTipText("İsminizi girin");
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });
        jTextField4.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTextField4InputMethodTextChanged(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
        });
        uyeOl.add(jTextField4);
        jTextField4.setBounds(160, 40, 128, 27);

        jTextField5.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jTextField5.setForeground(new java.awt.Color(54, 54, 54));
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.setToolTipText("Soyisminizi girin");
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
        });
        uyeOl.add(jTextField5);
        jTextField5.setBounds(390, 40, 128, 27);

        jTextField6.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jTextField6.setForeground(new java.awt.Color(200, 61, 38));
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.setToolTipText("Eposta adresinizi girin");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        uyeOl.add(jTextField6);
        jTextField6.setBounds(160, 80, 128, 27);

        jPasswordField1.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jPasswordField1.setForeground(new java.awt.Color(54, 54, 54));
        jPasswordField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPasswordField1.setToolTipText("Şifrenizi girin");
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });
        uyeOl.add(jPasswordField1);
        jPasswordField1.setBounds(390, 80, 128, 27);

        jButton29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gonder.png"))); // NOI18N
        jButton29.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gonderOver.png"))); // NOI18N
        jButton29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton29MouseClicked(evt);
            }
        });
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });
        uyeOl.add(jButton29);
        jButton29.setBounds(420, 150, 94, 31);

        jButton30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/temizle.png"))); // NOI18N
        jButton30.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/temizleOver.png"))); // NOI18N
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });
        uyeOl.add(jButton30);
        jButton30.setBounds(300, 150, 94, 31);

        jButton44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/goBack.png"))); // NOI18N
        jButton44.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/goBackOver.png"))); // NOI18N
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });
        uyeOl.add(jButton44);
        jButton44.setBounds(10, 164, 73, 31);

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anaEkranBg.png"))); // NOI18N
        uyeOl.add(jLabel16);
        jLabel16.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(uyeOl);
        uyeOl.setBounds(0, 0, 609, 215);

        sinifKontrol.setLayout(null);

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setMinimumSize(new java.awt.Dimension(609, 195));
        jPanel18.setLayout(null);

        jButton43.setText("<<Geri");
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });
        jPanel18.add(jButton43);
        jButton43.setBounds(10, 164, 80, 23);

        sinifKontrol.add(jPanel18);
        jPanel18.setBounds(0, 20, 609, 195);

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setMinimumSize(new java.awt.Dimension(609, 195));
        jPanel17.setLayout(null);

        jButton42.setText("<<Geri");
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });
        jPanel17.add(jButton42);
        jButton42.setBounds(10, 164, 80, 23);

        sinifKontrol.add(jPanel17);
        jPanel17.setBounds(0, 20, 609, 195);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setMinimumSize(new java.awt.Dimension(609, 195));
        jPanel16.setLayout(null);

        jButton41.setText("<<Geri");
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });
        jPanel16.add(jButton41);
        jButton41.setBounds(10, 164, 80, 23);

        sinifKontrol.add(jPanel16);
        jPanel16.setBounds(0, 20, 609, 195);

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setMinimumSize(new java.awt.Dimension(609, 195));
        jPanel15.setPreferredSize(new java.awt.Dimension(609, 195));
        jPanel15.setLayout(null);

        jButton40.setText("<<Geri");
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });
        jPanel15.add(jButton40);
        jButton40.setBounds(10, 164, 80, 23);

        sinifKontrol.add(jPanel15);
        jPanel15.setBounds(0, 20, 609, 195);

        jButton31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        jButton31.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });
        sinifKontrol.add(jButton31);
        jButton31.setBounds(50, 55, 77, 77);

        jButton32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sinifim.png"))); // NOI18N
        jButton32.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/sinifimOver.png"))); // NOI18N
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });
        sinifKontrol.add(jButton32);
        jButton32.setBounds(226, 55, 80, 79);

        jButton33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/odevler.png"))); // NOI18N
        jButton33.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/odevlerOver.png"))); // NOI18N
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });
        sinifKontrol.add(jButton33);
        jButton33.setBounds(372, 55, 79, 79);

        jButton34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gorevler.png"))); // NOI18N
        jButton34.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gorevlerOver.png"))); // NOI18N
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });
        sinifKontrol.add(jButton34);
        jButton34.setBounds(500, 55, 76, 78);

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sinifKontrolBg.png"))); // NOI18N
        sinifKontrol.add(jLabel19);
        jLabel19.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(sinifKontrol);
        sinifKontrol.setBounds(0, 0, 609, 215);

        siniflar.setLayout(null);

        jButton52.setText("YENİLE");
        jButton52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton52ActionPerformed(evt);
            }
        });
        siniflar.add(jButton52);
        jButton52.setBounds(260, 177, 94, 31);

        jScrollPane2.setBackground(java.awt.Color.white);
        jScrollPane2.setOpaque(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "SINIF ADI", "ŞEHİR/ÜLKE", "SEVİYE", "KULLANICI", "EĞİTMEN"
            }
        ));
        jTable1.setEnabled(false);
        jTable1.setGridColor(java.awt.Color.white);
        jTable1.setOpaque(false);
        jScrollPane2.setViewportView(jTable1);

        siniflar.add(jScrollPane2);
        jScrollPane2.setBounds(10, 25, 589, 145);

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anaEkranBg.png"))); // NOI18N
        siniflar.add(jLabel20);
        jLabel20.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(siniflar);
        siniflar.setBounds(0, 0, 609, 215);

        uyeEkrani.setLayout(null);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(null);

        jButton39.setText("<<Geri");
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton39);
        jButton39.setBounds(10, 164, 80, 23);

        uyeEkrani.add(jPanel14);
        jPanel14.setBounds(0, 20, 609, 195);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setLayout(null);

        jButton38.setText("<<Geri");
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });
        jPanel13.add(jButton38);
        jButton38.setBounds(10, 164, 80, 23);

        uyeEkrani.add(jPanel13);
        jPanel13.setBounds(0, 20, 609, 195);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setLayout(null);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Görev No", "Görev İsmi", "İlgili Görevler", "Görev Durumu"
            }
        ));
        jTable2.setEnabled(false);
        jScrollPane3.setViewportView(jTable2);

        jPanel11.add(jScrollPane3);
        jScrollPane3.setBounds(10, 30, 589, 130);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(153, 0, 0));
        jLabel30.setText("Görev Listesi");
        jPanel11.add(jLabel30);
        jLabel30.setBounds(12, 10, 106, 17);

        jButton36.setText("<<Geri");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });
        jPanel11.add(jButton36);
        jButton36.setBounds(10, 164, 80, 23);

        uyeEkrani.add(jPanel11);
        jPanel11.setBounds(0, 20, 609, 195);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setLayout(null);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(153, 0, 0));
        jLabel25.setText("Kişisel Bilgilerim");
        jPanel10.add(jLabel25);
        jLabel25.setBounds(12, 10, 106, 17);

        jLabel26.setText("İsim:");
        jPanel10.add(jLabel26);
        jLabel26.setBounds(10, 35, 106, 20);

        jLabel27.setText("Soyisim:");
        jPanel10.add(jLabel27);
        jLabel27.setBounds(10, 60, 106, 20);

        jLabel28.setText("Eposta:");
        jPanel10.add(jLabel28);
        jLabel28.setBounds(10, 90, 106, 20);

        jLabel29.setText("Şifre:");
        jPanel10.add(jLabel29);
        jLabel29.setBounds(10, 120, 106, 20);
        jPanel10.add(jTextField7);
        jTextField7.setBounds(140, 120, 130, 20);
        jPanel10.add(jTextField8);
        jTextField8.setBounds(140, 35, 130, 20);
        jPanel10.add(jTextField9);
        jTextField9.setBounds(140, 60, 130, 20);
        jPanel10.add(jTextField10);
        jTextField10.setBounds(140, 90, 130, 20);

        jButton37.setText("<<Geri");
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });
        jPanel10.add(jButton37);
        jButton37.setBounds(10, 164, 80, 23);

        jButton35.setText("GÜNCELLE");
        jPanel10.add(jButton35);
        jButton35.setBounds(140, 150, 130, 23);

        uyeEkrani.add(jPanel10);
        jPanel10.setBounds(0, 20, 609, 195);

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        jButton13.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        uyeEkrani.add(jButton13);
        jButton13.setBounds(50, 60, 77, 77);

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gorevler.png"))); // NOI18N
        jButton14.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gorevlerOver.png"))); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        uyeEkrani.add(jButton14);
        jButton14.setBounds(210, 60, 76, 78);

        jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/odevler.png"))); // NOI18N
        jButton15.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/odevlerOver.png"))); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        uyeEkrani.add(jButton15);
        jButton15.setBounds(340, 60, 79, 79);

        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sinifim.png"))); // NOI18N
        jButton16.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/sinifimOver.png"))); // NOI18N
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        uyeEkrani.add(jButton16);
        jButton16.setBounds(480, 60, 80, 79);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uyeEkraniBg.png"))); // NOI18N
        uyeEkrani.add(jLabel7);
        jLabel7.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(uyeEkrani);
        uyeEkrani.setBounds(0, 0, 609, 215);

        destekKutuphanesi.setLayout(null);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(null);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(153, 0, 0));
        jLabel42.setText("Sistem Ayarları");
        jPanel12.add(jLabel42);
        jLabel42.setBounds(10, 0, 87, 14);

        jButton53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton53.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton53ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton53);
        jButton53.setBounds(360, 0, 10, 10);

        destekKutuphanesi.add(jPanel12);
        jPanel12.setBounds(10, 60, 370, 90);

        jPanel9.setOpaque(false);
        jPanel9.setLayout(null);

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(153, 0, 0));
        jLabel41.setText("Shell Commands");
        jPanel9.add(jLabel41);
        jLabel41.setBounds(10, 0, 93, 14);

        jButton54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton54.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton54ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton54);
        jButton54.setBounds(360, 0, 10, 10);

        destekKutuphanesi.add(jPanel9);
        jPanel9.setBounds(10, 60, 370, 90);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(null);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(153, 0, 0));
        jLabel40.setText("Kullanıcı Hesapları");
        jPanel8.add(jLabel40);
        jLabel40.setBounds(10, 0, 102, 14);

        jButton55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton55.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton55ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton55);
        jButton55.setBounds(360, 0, 10, 10);

        destekKutuphanesi.add(jPanel8);
        jPanel8.setBounds(10, 60, 370, 90);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(null);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(153, 0, 0));
        jLabel39.setText("Donanım Ekle/Kaldır");
        jPanel7.add(jLabel39);
        jLabel39.setBounds(10, 0, 114, 14);

        jButton56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton56.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton56.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton56ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton56);
        jButton56.setBounds(360, 0, 10, 10);

        destekKutuphanesi.add(jPanel7);
        jPanel7.setBounds(10, 60, 370, 90);

        jPanel6.setOpaque(false);
        jPanel6.setLayout(null);

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(153, 0, 0));
        jLabel38.setText("İnternet");
        jPanel6.add(jLabel38);
        jLabel38.setBounds(10, 0, 48, 14);

        jButton57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton57.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton57.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton57ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton57);
        jButton57.setBounds(360, 0, 10, 10);

        destekKutuphanesi.add(jPanel6);
        jPanel6.setBounds(10, 60, 370, 90);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(null);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(153, 0, 0));
        jLabel37.setText("Libre Office");
        jPanel5.add(jLabel37);
        jLabel37.setBounds(10, 0, 63, 14);

        jButton58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton58.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton58.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton58ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton58);
        jButton58.setBounds(360, 0, 10, 10);

        destekKutuphanesi.add(jPanel5);
        jPanel5.setBounds(10, 60, 370, 90);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(null);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(153, 0, 0));
        jLabel17.setText("Hesap Makinesi");
        jPanel4.add(jLabel17);
        jLabel17.setBounds(10, 0, 88, 14);

        jButton59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x.png"))); // NOI18N
        jButton59.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/xOver.png"))); // NOI18N
        jButton59.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton59ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton59);
        jButton59.setBounds(360, 0, 10, 10);

        destekKutuphanesi.add(jPanel4);
        jPanel4.setBounds(10, 60, 370, 90);

        jTextField3.setForeground(new java.awt.Color(143, 143, 143));
        jTextField3.setBorder(null);
        destekKutuphanesi.add(jTextField3);
        jTextField3.setBounds(180, 24, 210, 15);

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/aramaButon.png"))); // NOI18N
        jButton17.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/aramaButonOver.png"))); // NOI18N
        destekKutuphanesi.add(jButton17);
        jButton17.setBounds(397, 20, 22, 24);

        jButton18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kategori.png"))); // NOI18N
        jButton18.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/kategoriOver.png"))); // NOI18N
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        destekKutuphanesi.add(jButton18);
        jButton18.setBounds(44, 156, 34, 24);

        jButton19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kategori.png"))); // NOI18N
        jButton19.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/kategoriOver.png"))); // NOI18N
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });
        destekKutuphanesi.add(jButton19);
        jButton19.setBounds(129, 156, 34, 24);

        jButton20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kategori.png"))); // NOI18N
        jButton20.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/kategoriOver.png"))); // NOI18N
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        destekKutuphanesi.add(jButton20);
        jButton20.setBounds(204, 156, 34, 24);

        jButton21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kategori.png"))); // NOI18N
        jButton21.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/kategoriOver.png"))); // NOI18N
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        destekKutuphanesi.add(jButton21);
        jButton21.setBounds(278, 156, 34, 24);

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kategori.png"))); // NOI18N
        jButton22.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/kategoriOver.png"))); // NOI18N
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });
        destekKutuphanesi.add(jButton22);
        jButton22.setBounds(353, 156, 34, 24);

        jButton23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kategori.png"))); // NOI18N
        jButton23.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/kategoriOver.png"))); // NOI18N
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });
        destekKutuphanesi.add(jButton23);
        jButton23.setBounds(437, 156, 34, 24);

        jButton24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kategori.png"))); // NOI18N
        jButton24.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/kategoriOver.png"))); // NOI18N
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });
        destekKutuphanesi.add(jButton24);
        jButton24.setBounds(530, 156, 34, 24);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kutuphaneBg.png"))); // NOI18N
        destekKutuphanesi.add(jLabel8);
        jLabel8.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(destekKutuphanesi);
        destekKutuphanesi.setBounds(0, 0, 609, 215);

        anaEkran.setLayout(null);

        jButton25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/egitimeBasla.png"))); // NOI18N
        jButton25.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/egitimebaslaOver.png"))); // NOI18N
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });
        anaEkran.add(jButton25);
        jButton25.setBounds(60, 40, 127, 126);

        jButton26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/siniflar.png"))); // NOI18N
        jButton26.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/siniflarOver.png"))); // NOI18N
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });
        anaEkran.add(jButton26);
        jButton26.setBounds(250, 40, 126, 126);

        jButton27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/destekKutuphanesi.png"))); // NOI18N
        jButton27.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/destekKutuphanesiOver.png"))); // NOI18N
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });
        anaEkran.add(jButton27);
        jButton27.setBounds(440, 40, 127, 126);

        jButton28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/benimSayfam.png"))); // NOI18N
        jButton28.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/benimSayfamOver.png"))); // NOI18N
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });
        anaEkran.add(jButton28);
        jButton28.setBounds(240, 180, 155, 29);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anaEkranBg.png"))); // NOI18N
        anaEkran.add(jLabel11);
        jLabel11.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(anaEkran);
        anaEkran.setBounds(0, 0, 609, 215);

        uyeGirisi.setBackground(new java.awt.Color(255, 255, 255));
        uyeGirisi.setLayout(null);

        jLabel10.setText("Kişisel");
        uyeGirisi.add(jLabel10);
        jLabel10.setBounds(132, 48, 80, 14);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        jButton8.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        uyeGirisi.add(jButton8);
        jButton8.setBounds(297, 40, 77, 77);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        jButton9.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        uyeGirisi.add(jButton9);
        jButton9.setBounds(405, 40, 77, 77);

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/yeniUylikButon.png"))); // NOI18N
        jButton10.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/yeniUylikButonOver.png"))); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        uyeGirisi.add(jButton10);
        jButton10.setBounds(300, 170, 277, 36);

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButon.png"))); // NOI18N
        jButton12.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/iconButonOver.png"))); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        uyeGirisi.add(jButton12);
        jButton12.setBounds(508, 40, 77, 77);

        jTextField1.setBackground(new java.awt.Color(243, 243, 243));
        jTextField1.setBorder(null);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        uyeGirisi.add(jTextField1);
        jTextField1.setBounds(80, 94, 140, 22);

        jTextField2.setBackground(new java.awt.Color(243, 243, 243));
        jTextField2.setBorder(null);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        uyeGirisi.add(jTextField2);
        jTextField2.setBounds(80, 129, 140, 22);

        jButton11.setBackground(new java.awt.Color(255, 255, 255));
        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/onayla.png"))); // NOI18N
        jButton11.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/onaylaOver.png"))); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        uyeGirisi.add(jButton11);
        jButton11.setBounds(173, 162, 74, 23);

        jRadioButton1.setBackground(new java.awt.Color(232, 232, 232));
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Beni Hatırla");
        jRadioButton1.setBorder(null);
        uyeGirisi.add(jRadioButton1);
        jRadioButton1.setBounds(61, 162, 100, 23);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uyeol.png"))); // NOI18N
        uyeGirisi.add(jLabel4);
        jLabel4.setBounds(29, 37, 244, 164);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uyeOlBg.png"))); // NOI18N
        uyeGirisi.add(jLabel9);
        jLabel9.setBounds(0, 0, 609, 215);

        jLayeredPane1.add(uyeGirisi);
        uyeGirisi.setBounds(0, 0, 609, 215);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
     
    //XML dosyaları içersinde istenilen bir veriyi değiştirmeye yarayan fonksiyon
    public void changeContent(String newname,String newemail) {
        try {
            doc = db.parse("uploadlar.xml");
            doc.normalize();
                
            Element root = doc.getDocumentElement();
            NodeList rootlist = root.getChildNodes();
            
            for(int i=0; i<rootlist.getLength(); i++) {
                Element person = (Element)rootlist.item(i);
                NodeList personlist = person.getChildNodes();
                Element name = (Element)personlist.item(0);
                NodeList namelist = name.getChildNodes();
                Text nametext = (Text)namelist.item(0);
                String oldname = nametext.getData();
                
                if(oldname.equals(newname)) {
                    Element email = (Element)personlist.item(1);
                    NodeList emaillist = email.getChildNodes();
                    Text emailtext = (Text)emaillist.item(0);
                    emailtext.setData(newemail);
                }
            }
        } catch (SAXException ex) { 
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //String içindeki verileri array içine almak için method
    public void toCharacterArray(String s) {
        int j = 0;
        for (int i = 0; i < s.length(); i++) {
            while(new Character(s.charAt(j)) != '-') {
                sinifKayitliUyeler[i] = sinifKayitliUyeler[i] + new Character(s.charAt(j));
                j++;
            }
        }
    }

    private void getUploadlarFromXML() {
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse("uploadlar.xml");
            doc.normalize();

            nodeListUploadlar = doc.getElementsByTagName("upload");          
            
            for (int i = 0; i < nodeListUploadlar.getLength(); i++) {             

                Node rootNode = nodeListUploadlar.item(i);
                Element myElmnt = (Element) rootNode;

                //Get uploadEdenOgrenci of current item
                NodeList uploadEdenOgrenciElmntLst = myElmnt.getElementsByTagName("uploadEdenOgrenci");
                Element uploadEdenOgrenciElmnt = (Element) uploadEdenOgrenciElmntLst.item(0);
                NodeList uploadEdenOgrenciN = uploadEdenOgrenciElmnt.getChildNodes();
                uploadEdenOgrenci = ((Node) uploadEdenOgrenciN.item(0)).getNodeValue();
                
                //Get uploadEdilenSinif of current item
                NodeList uploadEdilenSinifElmntLst = myElmnt.getElementsByTagName("uploadEdilenSinif");
                Element uploadEdilenSinifElmnt = (Element) uploadEdilenSinifElmntLst.item(0);
                NodeList uploadEdilenSinifN = uploadEdilenSinifElmnt.getChildNodes();
                uploadEdilenSinif = ((Node) uploadEdilenSinifN.item(0)).getNodeValue();
                
                //Get uploadEdilenGorevNosu of current item
                NodeList uploadEdilenGorevNosuElmntLst = myElmnt.getElementsByTagName("uploadEdilenGorevNosu");
                Element uploadEdilenGorevNosuElmnt = (Element) uploadEdilenGorevNosuElmntLst.item(0);
                NodeList uploadEdilenGorevNosuN = uploadEdilenGorevNosuElmnt.getChildNodes();
                uploadEdilenGorevNosu = ((Node) uploadEdilenGorevNosuN.item(0)).getNodeValue();
                
                //Get uploadEdilenDosyaninAdi of current item
                NodeList uploadEdilenDosyaninAdiElmntLst = myElmnt.getElementsByTagName("uploadEdilenDosyaninAdi");
                Element uploadEdilenDosyaninAdiElmnt = (Element) uploadEdilenDosyaninAdiElmntLst.item(0);
                NodeList uploadEdilenDosyaninAdiN = uploadEdilenDosyaninAdiElmnt.getChildNodes();
                uploadEdilenDosyaninAdi = ((Node) uploadEdilenDosyaninAdiN.item(0)).getNodeValue();
            }  
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
}
    
        private void getBeniHatirlaFromXML() {
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse("hatirla.xml");
            doc.normalize();
            
            /* beni hatırla XML dosyasının içinden en son beni hatırlayı seçerek giriş yapan üyenin bilgilerini 
               üyelik türüne bakarak alır.(XML dosyasında toplam 3 uye tutulabilir) */
            if(jLabel10.getText().equals(uyeTipleri[0])) {
                nodelistBeniHatirla = doc.getElementsByTagName("sonGirisK");
            } else if(jLabel10.getText().equals(uyeTipleri[1])) {
                nodelistBeniHatirla = doc.getElementsByTagName("sonGirisO");
            } else {
                nodelistBeniHatirla = doc.getElementsByTagName("sonGirisE");
            }
        
            for (int i = 0; i < nodelistBeniHatirla.getLength(); i++) {             

                Node rootNode = nodelistBeniHatirla.item(i);
                Element myElmnt = (Element) rootNode;

                //Get sonGirisEposta of current item
                NodeList sonGirisEpostaElmntLst = myElmnt.getElementsByTagName("sonGirisEposta");
                Element sonGirisEpostaElmnt = (Element) sonGirisEpostaElmntLst.item(0);
                NodeList sonGirisEpostaN = sonGirisEpostaElmnt.getChildNodes();
                beniHatirlaEposta = ((Node) sonGirisEpostaN.item(0)).getNodeValue();
                
                //Get sonGirisSifre of current item
                NodeList sonGirisSifreElmntLst = myElmnt.getElementsByTagName("sonGirisSifre");
                Element sonGirisSifreElmnt = (Element) sonGirisSifreElmntLst.item(0);
                NodeList sonGirisSifreN = sonGirisSifreElmnt.getChildNodes();
                beniHatirlaSifre = ((Node) sonGirisSifreN.item(0)).getNodeValue();
                
                //Get sonGirisStatu of current item
                NodeList sonGirisStatuElmntLst = myElmnt.getElementsByTagName("sonGirisStatu");
                Element sonGirisStatuElmnt = (Element) sonGirisStatuElmntLst.item(0);
                NodeList sonGirisStatuN = sonGirisStatuElmnt.getChildNodes();
                beniHatirlaUyeTipi = ((Node) sonGirisStatuN.item(0)).getNodeValue();
                
                if(!(beniHatirlaEposta.equals("") && beniHatirlaSifre.equals(""))) {
                    jLabel10.setText(beniHatirlaUyeTipi);
                    jTextField1.setText(beniHatirlaEposta);
                    jTextField2.setText(beniHatirlaSifre);
                }
            }  
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
}
    
    //request 1 olarak geldiyse eğitmenin sınıflarını listeler
    @SuppressWarnings("empty-statement")
    private void getSiniflarFromXML(int request) {
        try {                          
               dbf = DocumentBuilderFactory.newInstance();
               db = dbf.newDocumentBuilder();
               doc = db.parse("siniflar.xml");
               doc.normalize();

               nodeListSiniflar = doc.getElementsByTagName("sinifAdi");          

               for (int i = 0; i < nodeListSiniflar.getLength(); i++) {             

                   Node rootNode = nodeListSiniflar.item(i);
                   Element myElmnt = (Element) rootNode;

                   //Get sinifAdi of current item
                   NodeList sinifAdiElmntLst = myElmnt.getElementsByTagName("adi");
                   Element sinifAdiElmnt = (Element) sinifAdiElmntLst.item(0);
                   NodeList sinifAdiN = sinifAdiElmnt.getChildNodes();
                   sinifAdi = ((Node) sinifAdiN.item(0)).getNodeValue();

                   //Get sehir of current item
                   NodeList sehirElmntLst = myElmnt.getElementsByTagName("sehir");
                   Element sehirElmnt = (Element) sehirElmntLst.item(0);
                   NodeList sehirN = sehirElmnt.getChildNodes();
                   sinifSehri = ((Node) sehirN.item(0)).getNodeValue();

                   //Get ulke of current item
                   NodeList ulkeElmntLst = myElmnt.getElementsByTagName("ulke");
                   Element ulkeElmnt = (Element) ulkeElmntLst.item(0);
                   NodeList ulkeN = ulkeElmnt.getChildNodes();
                   sinifUlkesi = ((Node) ulkeN.item(0)).getNodeValue();
                   
                   //Get kullaniciSayisi of current item
                   NodeList kullaniciSayisiElmntLst = myElmnt.getElementsByTagName("kullaniciSayisi");
                   Element kullaniciSayisiElmnt = (Element) kullaniciSayisiElmntLst.item(0);
                   NodeList kullaniciSayisiN = kullaniciSayisiElmnt.getChildNodes();
                   sinifKullaniciSayisi = ((Node) kullaniciSayisiN.item(0)).getNodeValue();

                   //Get sinifKapasitesi of current item
                   NodeList sinifKapasitesiElmntLst = myElmnt.getElementsByTagName("sinifKapasitesi");
                   Element sinifKapasitesiElmnt = (Element) sinifKapasitesiElmntLst.item(0);
                   NodeList sinifKapasitesiN = sinifKapasitesiElmnt.getChildNodes();
                   sinifKapasitesi = ((Node) sinifKapasitesiN.item(0)).getNodeValue();
                   
                   //Get egitmen of current item
                   NodeList egitmenElmntLst = myElmnt.getElementsByTagName("egitmen");
                   Element egitmenElmnt = (Element) egitmenElmntLst.item(0);
                   NodeList egitmenN = egitmenElmnt.getChildNodes();
                   sinifEgitmeni = ((Node) egitmenN.item(0)).getNodeValue();
                   
                   if(request == 1) {   
                       if(sinifEgitmeni.equalsIgnoreCase(girisYapanKullaniciIsmi)) {
                           jLabel24.setText("Sınıflar: ");
                           jLabel23.setText(jLabel23.getText()+sinifAdi);                     
                       }                
                   }

                   //Get kayitliUyeler of current item
                   NodeList kayitliUyelerElmntLst = myElmnt.getElementsByTagName("kayitliUyeler");
                   Element kayitliUyelerElmnt = (Element) kayitliUyelerElmntLst.item(0);
                   NodeList kayitliUyelerN = kayitliUyelerElmnt.getChildNodes();
                   sinifKayitliUyelerTemp = ((Node) kayitliUyelerN.item(0)).getNodeValue();
                   //sinifiKayitliUyelerTemp içine alınan stringi delimiter mantığı şeklinde karakter karakter okuyarak sinifiKayitliUyeler array'ine yerleştirir
//                   toCharacterArray(sinifKayitliUyelerTemp);

                   //Get sinifSeviyesi of current item
                   NodeList sinifSeviyesiElmntLst = myElmnt.getElementsByTagName("sinifSeviyesi");
                   Element sinifSeviyesiElmnt = (Element) sinifSeviyesiElmntLst.item(0);
                   NodeList sinifSeviyesiN = sinifSeviyesiElmnt.getChildNodes();
                   sinifSeviyesi = ((Node) sinifSeviyesiN.item(0)).getNodeValue();
                   
                   getGorevlerFromXML(1,99);

                   setSinifListesiData(sinifAdi, sinifSehri, sinifSeviyesi, sinifKullaniciSayisi, sinifEgitmeni, sinifKapasitesi, gorevAdiTutucu, i);
                   //JOptionPane.showMessageDialog(null, "Sınıflar XML dosya hatası!");
                 } 
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
    }
    
    // Task isimlerini alır. usage = 1 ise sinifseviyesine göre task ismi alır, usage = 2 ise processId'ye bakar 
    // ve processId id'ye eşitse jLabel1 ve jLabel3'e ilgili değerleri atar değer atar.
    private void getGorevlerFromXML(int usage, int processId) {
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.parse("tasks.xml");
            doc.normalize();

            nodeListGorevler = doc.getElementsByTagName("item");          
            
            for (int i = 0; i < nodeListGorevler.getLength(); i++) {             
                
                Node rootNode = nodeListGorevler.item(i);
                Element myElmnt = (Element) rootNode;

                //Get id of current item
                NodeList idElmntLst = myElmnt.getElementsByTagName("id");
                Element idElmnt = (Element) idElmntLst.item(0);
                NodeList idN = idElmnt.getChildNodes();
                gorevId = ((Node) idN.item(0)).getNodeValue();
                
                //Get name of current item
                NodeList nameElmntLst = myElmnt.getElementsByTagName("name");
                Element nameElmnt = (Element) nameElmntLst.item(0);
                NodeList nameN = nameElmnt.getChildNodes();
                gorevAdi = ((Node) nameN.item(0)).getNodeValue();

                //Get hints of current item
                NodeList hintsElmntLst = myElmnt.getElementsByTagName("hints");
                Element hintsElmnt = (Element) hintsElmntLst.item(0);
                NodeList hintsN = hintsElmnt.getChildNodes();
                gorevAciklamasi = ((Node) hintsN.item(0)).getNodeValue();

                //Get relatedTasks of current item
                NodeList relatedTasksElmntLst = myElmnt.getElementsByTagName("relatedTasks");
                Element relatedTasksElmnt = (Element) relatedTasksElmntLst.item(0);
                NodeList relatedTasksN = relatedTasksElmnt.getChildNodes();
                gorevIlgiliGorevler = ((Node) relatedTasksN.item(0)).getNodeValue();

                //Get processName of current item
                NodeList processNameElmntLst = myElmnt.getElementsByTagName("processName");
                Element processNameElmnt = (Element) processNameElmntLst.item(0);
                NodeList processNameN = processNameElmnt.getChildNodes();
                gorevExeAdi = ((Node) processNameN.item(0)).getNodeValue();
                
                if(usage == 1) {         
                    if (gorevId.equals(sinifSeviyesi)) {         
                     gorevAdiTutucu = gorevAdi;
                    }    
                } else if(usage == 2) {
                    if (gorevId.equals(String.valueOf(processId))) {  
                        jLabel1.setText("GÖREV "+gorevId+":");
                        jLabel3.setText(gorevAdi);
                        gorevExeAdiTutucu = gorevExeAdi;
                        jTextArea1.setText(gorevAciklamasi);
                    }
                }
                
            }  
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    //bu fonksiyon uye listesinde yer alan verileri kontrol eder
    //eğer selection parametresi 0 olarak geldiyse sadece uye listesini node içine alır.(DEFAULT)
    //eğer selection parametresi 1 olarak geldiyse uye girisini kontrol eder.
    //eğer selection parametresi 2 olarak geldiyse kişisel uyenin seviyesi update edilir.
    private void getUyelerFromXML(int selection) {
        try {                  
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            //açılacak XML dosyasının yolu ve adı
            doc = db.parse("uyeler.xml");
            doc.normalize();

            if(jLabel10.getText().equalsIgnoreCase(uyeTipleri[0])) {
                nodeListUyeler = doc.getElementsByTagName("uyeIdK");
                girisYapanKullaniciTuru = 0;
            } else if(jLabel10.getText().equalsIgnoreCase(uyeTipleri[1])) {
                nodeListUyeler = doc.getElementsByTagName("uyeIdO");  
                girisYapanKullaniciTuru = 1;
            } else if(jLabel10.getText().equalsIgnoreCase(uyeTipleri[2])) {
                nodeListUyeler = doc.getElementsByTagName("uyeIdE");  
                girisYapanKullaniciTuru = 2;
            } else {
                //bu kısım gereksiz çünkü kullanıcı kullanıcıTipini taşıyan jLabel'a müdahale edemez ama koymakta fayda var
                JOptionPane.showMessageDialog(null, "Geçersiz kullanıcı türü!");
            }


            for (int i = 0; i < nodeListUyeler.getLength(); i++) {             

                Node rootNode = nodeListUyeler.item(i);
                Element myElmnt = (Element) rootNode;

                //Get isim of current item
                NodeList isimElmntLst = myElmnt.getElementsByTagName("isim");
                Element isimElmnt = (Element) isimElmntLst.item(0);
                NodeList isimN = isimElmnt.getChildNodes();
                uyelerIsim = ((Node) isimN.item(0)).getNodeValue();

                //Get soyisim of current item
                NodeList soyisimElmntLst = myElmnt.getElementsByTagName("soyisim");
                Element soyisimElmnt = (Element) soyisimElmntLst.item(0);
                NodeList soyisimN = soyisimElmnt.getChildNodes();
                uyelerSoyisim = ((Node) soyisimN.item(0)).getNodeValue();

                //Get sifre of current item
                NodeList sifreElmntLst = myElmnt.getElementsByTagName("sifre");
                Element sifreElmnt = (Element) sifreElmntLst.item(0);
                NodeList sifreN = sifreElmnt.getChildNodes();
                uyelerSifre = ((Node) sifreN.item(0)).getNodeValue();

                //Get eposta of current item
                NodeList epostaElmntLst = myElmnt.getElementsByTagName("eposta");
                Element epostaElmnt = (Element) epostaElmntLst.item(0);
                NodeList epostaN = epostaElmnt.getChildNodes();
                uyelerEposta = ((Node) epostaN.item(0)).getNodeValue();
                
                //kişisel üyenin seviyesini yeniler
                if(selection == 2){ 
                    if(girisYapanKullaniciIsmi.equalsIgnoreCase(uyelerIsim) && girisYapanKullaniciEpostasi.equals(uyelerEposta)) {
                        if(girisYapanKullaniciTuru == 0) {
                            //Get seviye of current item
                            NodeList seviyeElmntLst = myElmnt.getElementsByTagName("seviye");
                            Element seviyeElmnt = (Element) seviyeElmntLst.item(0);
                            NodeList seviyeN = seviyeElmnt.getChildNodes();
                            ((Node) seviyeN.item(0)).setNodeValue(gorevId);
                        }
                    }
                }
          
                //üye girişini kontrol eder
                if(selection == 1){
                    userLogin(myElmnt, (i+1), nodeListUyeler.getLength());             
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    //Üye kayıt olurken isim ve soyisminin ilk harflerini büyük harf yapmak için fonksiyon
    public static void firstLetterToUppercase(JTextField input) {
		String text = input.getText();
		StringBuilder result = new StringBuilder();
		char ch;
		for (int i = 0; i < text.length(); i++) {
			ch = text.charAt(i);
			if (Character.isLetter(ch) && ((i == 0) || !Character.isLetter(text.charAt(i - 1)))){
				result.append(Character.toUpperCase(ch));
			} else {
				result.append(Character.toLowerCase(ch));
			}
		}
		input.setText(result.toString());
    }
    
    private void yeniKayitEkleToXML() {
        if(jTextField4.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kayıt olmak için isminizi girmelisiniz!");
        } else if(!(jTextField4.getText().matches("^[a-zA-ZöçşğüıÖÇŞĞÜİ ]+$"))) {
            JOptionPane.showMessageDialog(null, "İsminiz sadece karakterlerden oluşabilir!");
        } else if(jTextField4.getText().length() < 3) {
            JOptionPane.showMessageDialog(null, "İsminiz iki veya daha az karakterden oluşamaz!");
        } else if(jTextField5.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kayıt olmak için soyisminizi girmelisiniz!");
        } else if(!(jTextField5.getText().matches("^[a-zA-ZöçşğüıÖÇŞĞÜİ]+$"))) {
            JOptionPane.showMessageDialog(null, "Soyisminiz sadece karakterlerden oluşabilir!");
        } else if(jTextField5.getText().length() < 3) {
            JOptionPane.showMessageDialog(null, "Soyisminiz iki veya daha az karakterden oluşamaz!");
        } else if(jTextField6.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kayıt olmak için eposta adresinizi girmelisiniz!");
        } else if(!(jTextField6.getText().contains("@")) || jTextField6.getText().length() < 5) {
            JOptionPane.showMessageDialog(null, "Lütfen geçerli bir eposta adresi giriniz!");
        } else if(jPasswordField1.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kayıt olmak için bir şifre belirlemelisiniz!");
        } else if(jPasswordField1.getText().length() < 4 || jPasswordField1.getText().length() > 16) {
            JOptionPane.showMessageDialog(null, "Şifre uzunluğu 4-16 karakter aralığında olmalıdır!");
        } else {
            
            try {
                dbf = DocumentBuilderFactory.newInstance();
                db = dbf.newDocumentBuilder();
                doc = db.parse("uyeler.xml");
            } 
            catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(null, "Üyeler XML dosyası bulunamadı!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Dosya ayrıştırma(parsing) hatası!");
            }

            Element root = doc.getDocumentElement();

            //NodeList deleteElement = root.getElementsByTagName("staff");
            //Node deleteNode= deleteElement.item(0);
            //root.removeChild(deleteNode);
            Element uyelikTuruAdi;
            Element uyeTipiElement;       

            Node newLine = doc.createTextNode("\n");

            if(jRadioButton4.isSelected() == true) {
                uyelikTuruAdi = doc.createElement("kisisel");
                uyeTipiElement = doc.createElement("uyeIdK");
            } else if (jRadioButton3.isSelected() == true) {
                uyelikTuruAdi = doc.createElement("egitmen");
                uyeTipiElement = doc.createElement("uyeIdE");
            } else {
                uyelikTuruAdi = doc.createElement("ogrenci");
                uyeTipiElement = doc.createElement("uyeIdO");
            }

            //üye ismini ekle
            Element isim = doc.createElement("isim");
            Node isimNode = doc.createTextNode(jTextField4.getText());
            isim.appendChild(isimNode);
            uyeTipiElement.appendChild(isim);


            //yeni uyenin soyismini ekle
            Element soyisim = doc.createElement("soyisim");
            Node soyisimNode = doc.createTextNode(jTextField5.getText());
            soyisim.appendChild(soyisimNode);
            uyeTipiElement.appendChild(soyisim);


            //yeni uyenin sifresini ekle
            Element sifre = doc.createElement("sifre");
            Node sifreNode = doc.createTextNode(jPasswordField1.getText());
            sifre.appendChild(sifreNode);
            uyeTipiElement.appendChild(sifre);

            //yeni uyenin eposta adresini ekle
            Element eposta = doc.createElement("eposta");
            Node epostaNode = doc.createTextNode(jTextField6.getText());
            eposta.appendChild(epostaNode);
            uyeTipiElement.appendChild(eposta);

            //kayıt olan kişi öğretmen değilse seviye eklenir
            if (jRadioButton3.isSelected() == false) {
                //yeni uyeye seviye ekle (1 olarak eklenir)
                Element seviye = doc.createElement("seviye");
                String str_seviye="1";
                Node seviyeNode = doc.createTextNode(str_seviye);
                seviye.appendChild(seviyeNode);
                uyeTipiElement.appendChild(seviye);
            }

            //Node'ları birbirlerine ekler       
            uyelikTuruAdi.appendChild(uyeTipiElement);      
            root.appendChild(uyelikTuruAdi);      
            root.appendChild(newLine);      

            //Node StaffNode=(Node)updateElement;

            try{
                String outputURL = "uyeler.xml";

                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new FileOutputStream(outputURL));

                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();

                transformer.transform(source, result);
            } catch (TransformerException ex) {
                Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
            } 
            JOptionPane.showMessageDialog(null, jTextField4.getText()+" "+jTextField5.getText()+", üyelik kaydınız başarıyla gerçekleştirildi. Hesabınızı kullanmaya başlayabilirsiniz!");
            anaPanellerArasiGecis(1);

            //kayıt ol alanındaki verileri temizler
            jTextField4.setText("");
            jTextField5.setText("");
            jTextField6.setText("");
            jPasswordField1.setText("");
            jRadioButton2.setSelected(true);
        }
        
    }
    /* uye girisini eposta, sifre ve uyeTipi verilerine bakarak kontrol edecek fonksiyon
       bu fonksiyon ayrıca üyenin diğer bilgilerini de alır ve üye türüne göre alınması gerekli diğer bilgileri
       alan ilgili fonksiyonları da çağırır/çalıştırır ve o bilgileri de alır*/
    private void userLogin(Element myElmnt,int gelenUyeSirasi, int toplamUyeSayisi) {
        if(jTextField1.getText().equals(uyelerEposta) && jTextField2.getText().equals(uyelerSifre)) {               
            jLabel21.setVisible(true);
            jLabel22.setVisible(true);     
            jLabel22.setText(uyelerIsim +" "+uyelerSoyisim);
            jLabel31.setVisible(true); 
            
            //giris yapan kullanıcının gerekli bilgilerini değişkenlere atar
            girisYapanKullaniciEpostasi = uyelerEposta;
            girisYapanKullaniciIsmi = uyelerIsim;
            girisYapanKullaniciSoyismi = uyelerSoyisim;
                    
            /* add actionListener for jLabel31 to assign underline when mouse over and normal when mouse out */
            jLabel31.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    jLabel31.setText("<html><u>Çıkış</u></html>");
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    jLabel31.setText("Çıkış");
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    
                    //kullanıcı timer fonksiyonu çalışırken çıkmak isterse işlem anında kontrol edilsin diye kalan zaman -1 saniyeye ayarlanır
                    interval = -1;
                    
                    //gişir sırasında üst menü iconlarını giriş yapılmıyorsa pasif tutmak için değişkene 0 atanır
                    ustMenuSecimi = 0;
        
                    //geçerli görevi birinci göreve eşitler
                    gecerliGorev = 1;
        
                    //bütün node'ları sıfırlar
                    nodeListGorevler = null; 
                    nodeListUyeler = null;
                    nodeListSiniflar = null;
                    nodeListUploadlar = null;
                    nodelistBeniHatirla = null;
                    
                    //butun XML dosyalarından gelen degiskenlerin içini boşaltır
                    XML = null;
                    sinifKayitliUyeler = null;
                    gorevAdiTutucu = null;
                    gorevExeAdiTutucu = null;

                    //bütün gorevlerin, uyelerin, siniflarin ve uploadların listesini alir ilgili node'ların içlerine listeler   
                    getGorevlerFromXML(2,gecerliGorev);
                    getGorevlerFromXML(0,99);                   
                    getSiniflarFromXML(99);
                    getUyelerFromXML(0);
                    getBeniHatirlaFromXML();
                    getUploadlarFromXML();

                    //program içinde kullanılan bütün butonların arkaplanlarını saydamlaştırır
                    setTransparentToButtons();

                    //program içinde kullanılan bütün alt panelleri (18 tane) saydamlaştırır
                    setSubPanelToVisible(99);

                    //ilkEkran dışındaki bütün 12 ana paneli program başlarken görünmez hale getirir
                    anaPanellerArasiGecis(6);

                    //gorevler panelindeki ileri butonunu disable yapar ve iconunu disable icon ile değiştirir
                    jButton5.setEnabled(false);
                    jButton5.setIcon(nextDisabled);

                    //ustMenu panelinde yer alan Hoşgeldiniz text'ini, Kullanıcı ismi ve Çıkış text'ini taşıyan jLabel'lar açılışta gizlenir (giriş yapıldıktan sonra görünür hale getirilir)
                    jLabel21.setVisible(false);
                    jLabel22.setText("");
                    jLabel23.setText("");
                    jLabel24.setText("");
                    jLabel31.setVisible(false);

                    JOptionPane.showMessageDialog(null, "Başarılı bir şekilde çıkış yapıldı!");
                    jLabel31.setText("Çıkış");
                    jLabel31.removeMouseListener(this);
                }
            });

            if(jLabel10.getText().equals(uyeTipleri[0])) {
                //kişisel kullanıcının seviyesini alır
                NodeList seviyeElmntLst = myElmnt.getElementsByTagName("seviye");
                Element seviyeElmnt = (Element) seviyeElmntLst.item(0);
                NodeList seviyeN = seviyeElmnt.getChildNodes();
                uyelerKisiselSeviye = ((Node) seviyeN.item(0)).getNodeValue();
                jLabel24.setText("Seviye: ");
                jLabel23.setText(uyelerKisiselSeviye);
                //giriş başarılı olursa kişisel kullanıcı anaEkranına gider
                anaPanellerArasiGecis(9);
            } else if(jLabel10.getText().equals(uyeTipleri[1])){
                //öğrenci için seviye kısmına "Önce bir sınıf seçin" yazar
                jLabel24.setText("Seviye: ");
                jLabel23.setText("Önce bir sınıf seçin");
                //giriş başarılı olursa öğrenci anaEkranına gider
                anaPanellerArasiGecis(2);
            } else {
                //seviye yerine eğitmenin kayıtlı olduğu sınıfları yazar
                //bu işlem getSiniflarFromXML'de gerçekleştirilecek, parametre olarak 1 gonderilirse;
                //siniflar.XML'i aç sınıf eğitmeni eğitmen adıyla uyuyorsa sınıf adını al
                getSiniflarFromXML(1);
                anaPanellerArasiGecis(11);
            }
        } else {
            if(gelenUyeSirasi == toplamUyeSayisi && jLabel31.isVisible() == false) {
                JOptionPane.showMessageDialog(null, "Eposta veya şifre yanlış!");
            }       
    }

}
      
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        buttonClickSound();
        gecerliGorev++; 
        getGorevlerFromXML(2,gecerliGorev);
        getUyelerFromXML(2); //kişisel üyenin seviyesini XML'de artırmak için fonksiyona parametre olarak 2 gönderilir
        jButton4.setEnabled(true);
        jButton4.setIcon(previousEnabled);
        jButton5.setEnabled(false);
        jButton5.setIcon(nextDisabled);
        jButton6.setEnabled(true);
        jButton7.setEnabled(true);
        flag = false;       
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        buttonClickSound();
        startTimer();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        buttonClickSound();
        interval = -2;
        getProcesses();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void ogrenciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ogrenciActionPerformed
        buttonClickSound();
        jLabel10.setText(uyeTipleri[1]);
        getBeniHatirlaFromXML();
        anaPanellerArasiGecis(1);
    }//GEN-LAST:event_ogrenciActionPerformed

    private void egitmenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_egitmenActionPerformed
        buttonClickSound();
        jLabel10.setText(uyeTipleri[2]);
        getBeniHatirlaFromXML();
        anaPanellerArasiGecis(1);
    }//GEN-LAST:event_egitmenActionPerformed

    private void kisiselKullanimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kisiselKullanimActionPerformed
        buttonClickSound();
        jLabel10.setText(uyeTipleri[0]);
        getBeniHatirlaFromXML();
        anaPanellerArasiGecis(1);
    }//GEN-LAST:event_kisiselKullanimActionPerformed

    private void yeniHesapOlusturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yeniHesapOlusturActionPerformed
        buttonClickSound();      
        anaPanellerArasiGecis(8);
        jTextField4.requestFocus();
    }//GEN-LAST:event_yeniHesapOlusturActionPerformed

    private void girisKontrolEt() {
        //kullanıcı giriş yapmamış, giriş kontrolü yapılabilir
        if(jLabel22.getText().equals("")) {
            //giriş panelinde değilse, üst menü iconları kullanılmak isteniyorsa, gerekli açıklamalar yapılır
            if(uyeGirisi.isVisible() == false) {
                
                    if(ustMenuSecimi == 1) {
                        if(ilkEkran.isVisible() == true) {
                            JOptionPane.showMessageDialog(null, "Şu anda zaten açılış ekranındasınız!");
                        } else {
                            anaPanellerArasiGecis(6);
                        }                  
                    } else if(ustMenuSecimi == 2) {
                        JOptionPane.showMessageDialog(null, "Üye ana ekranınıza erişebilmeniz için önce giriş yapmalısınız!");
                    } else {
                        anaPanellerArasiGecis(1);
                    }
                      
            } else {
                if(ustMenuSecimi != 0) {
                    if(ustMenuSecimi == 1) {
                        anaPanellerArasiGecis(6);
                    } else {
                        JOptionPane.showMessageDialog(null, "Üye ana ekranınıza erişebilmeniz için önce giriş yapmalısınız!");
                    }                 
                } else if (!(jTextField1.getText().contains("@"))) {
                    if(jTextField1.getText().equals("")) {
                        JOptionPane.showMessageDialog(null, "Lütfen eposta adresinizi giriniz.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Lütfen geçerli bir eposta giriniz.");
                    }              
                } else if (jTextField2.getText().equals("") || jTextField2.getText().contains(" ")) {
                    if (jTextField2.getText().contains(" ")) {
                    JOptionPane.showMessageDialog(null, "Lütfen geçerli bir şifre giriniz.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Lütfen şifrenizi giriniz.");
                    }                    
                } else {
                    //userLogin getUyelerFromXML fonksiyonun içinde, çalıştırmak için parametre olarak 1 yollanmalı
                    getUyelerFromXML(1);
                }               
           }                       
       } else {
            if(ustMenuSecimi == 2) {
                if(girisYapanKullaniciTuru == 0) {
                    if(anaEkranKisisel.isVisible() == true) {
                        JOptionPane.showMessageDialog(null, jLabel22.getText()+ ", zaten kullanıcı ana panelinizdesiniz!");
                    } else {
                        anaPanellerArasiGecis(9);
                    }               
                } else if(girisYapanKullaniciTuru == 1) {
                    if(anaEkran.isVisible() == true) {
                        JOptionPane.showMessageDialog(null, jLabel22.getText()+ ", zaten kullanıcı ana panelinizdesiniz!");
                    } else {
                        anaPanellerArasiGecis(2);
                    }
                } else {
                    if(anaEkranEgitmen.isVisible() == true) {
                        JOptionPane.showMessageDialog(null, jLabel22.getText()+ ", zaten kullanıcı ana panelinizdesiniz!");
                    } else {
                        anaPanellerArasiGecis(11);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, jLabel22.getText()+ ", zaten giriş yaptınız!");
            }
           
       }
    }
    private void anasayfaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anasayfaActionPerformed
        buttonClickSound();
        ustMenuSecimi = 1;
        girisKontrolEt();
    }//GEN-LAST:event_anasayfaActionPerformed

    private void kisiselActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kisiselActionPerformed
        buttonClickSound();
        ustMenuSecimi = 2;
        girisKontrolEt();
        getBeniHatirlaFromXML();
    }//GEN-LAST:event_kisiselActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(1);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        buttonClickSound();
        jTextField11.setText("");
        setSubPanelToVisible(2);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(3);
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(4);
        getSiniflarFromXML(99);
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(7);
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(0);
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        buttonClickSound();
        jLabel10.setText(uyeTipleri[1]);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        buttonClickSound();
        jLabel10.setText(uyeTipleri[2]);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(3);
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(4);
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(5);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(6);
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(7);
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(8);
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(11);
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        buttonClickSound();
        gecerliGorev--;
        getGorevlerFromXML(2,gecerliGorev);
        if(gecerliGorev == 1) {
            jButton4.setEnabled(false);
            jButton4.setIcon(previousDisabled);
        }   
        jButton5.setEnabled(true);
        jButton5.setIcon(nextEnabled);
        jButton6.setEnabled(true);
        jButton7.setEnabled(true);
        flag = false; 
//        
//        gecerliGorev--;
//        if(gecerliGorev <= 2) {     
//            jButton4.setEnabled(false);
//            jButton4.setIcon(previousDisabled);
//            jLabel1.setText("GÖREV "+(gecerliGorev)+":");
//            getGorevlerFromXML(2,gecerliGorev);
//        }
//        jButton5.setEnabled(true);
//        jButton5.setIcon(nextEnabled);
//        jButton6.setEnabled(true);
//        jButton7.setEnabled(true);
//       
//        flag = false; 
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(8);
        jTextField4.requestFocus();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        buttonClickSound();
        ustMenuSecimi = 0;
        girisKontrolEt();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        ustMenuSecimi = 0;
        girisKontrolEt();
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(9);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(10);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton38ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(12);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
       buttonClickSound();
        setSubPanelToVisible(13);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        ustMenuSecimi = 0;
        girisKontrolEt();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        buttonClickSound();
        jLabel10.setText("Kişisel");
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(14);
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(15);
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(16);
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(17);
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(6);
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        buttonClickSound();
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jPasswordField1.setText("");
        jRadioButton2.setSelected(true);
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(7);
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        buttonClickSound();
        anaPanellerArasiGecis(3);
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        buttonClickSound();
        
    }//GEN-LAST:event_jButton47ActionPerformed

    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        buttonClickSound();
        
    }//GEN-LAST:event_jButton48ActionPerformed

    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        buttonClickSound();
        
    }//GEN-LAST:event_jButton49ActionPerformed

    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        buttonClickSound();
        
    }//GEN-LAST:event_jButton50ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        buttonClickSound();
        yeniKayitEkleToXML();
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton53ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton53ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton53ActionPerformed

    private void jButton54ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton54ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton54ActionPerformed

    private void jButton55ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton55ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton55ActionPerformed

    private void jButton56ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton56ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton56ActionPerformed

    private void jButton57ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton57ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton57ActionPerformed

    private void jButton58ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton58ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton58ActionPerformed

    private void jButton59ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton59ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton59ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        yeniKayitEkleToXML();
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        yeniKayitEkleToXML();
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        yeniKayitEkleToXML();
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        yeniKayitEkleToXML();
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jButton29MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton29MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton29MouseClicked

    private void jButton60ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton60ActionPerformed
        buttonClickSound();
        jTextField11.setText("");
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton60ActionPerformed

    private void jButton61ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton61ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton61ActionPerformed

    private void jButton62ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton62ActionPerformed
        buttonClickSound();
        setSubPanelToVisible(99);
    }//GEN-LAST:event_jButton62ActionPerformed

    private void jTextField4InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTextField4InputMethodTextChanged
//        firstLetterToUppercase(jTextField4);
    }//GEN-LAST:event_jTextField4InputMethodTextChanged

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        firstLetterToUppercase(jTextField4);
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        firstLetterToUppercase(jTextField5);
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jButton52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton52ActionPerformed
        getSiniflarFromXML(99);
        JOptionPane.showMessageDialog(null, "Tablo başarıyla yenilendi!");
    }//GEN-LAST:event_jButton52ActionPerformed

    private void jButton63ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton63ActionPerformed
        dosyaSecici();        
        /* AŞAĞIDAKİ METHOD ALTERNATİF OLARAK TUTULDU */
        //if(FileChooser.isVisible() == false) {
        //  FileChooser.setVisible(true);
        //} else {
        //  JOptionPane.showMessageDialog(null, "Lüften yüklemek istediğiniz dosyayı seçiniz!");
        //}      
    }//GEN-LAST:event_jButton63ActionPerformed

    private void jTextField11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField11ActionPerformed
        dosyaSecici();
    }//GEN-LAST:event_jTextField11ActionPerformed

    private void jTextField11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField11MouseClicked
        dosyaSecici();
    }//GEN-LAST:event_jTextField11MouseClicked

    private void jButton64ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton64ActionPerformed
        DosyaYukle();
    }//GEN-LAST:event_jButton64ActionPerformed
   
    //Bu fonksiyon dosya secici penceresini açar ve seçilen dosyanın yolunu ilgili textfield'e atar
    public void dosyaSecici() {
        JFileChooser dosyaSecici = new JFileChooser();
        dosyaSecici.showOpenDialog(null);
        File secilenDosya = dosyaSecici.getSelectedFile();
        secilenDosyaninYolu = secilenDosya.getAbsolutePath();
        jTextField11.setText(secilenDosyaninYolu);
    }
    
    public void setSinifListesiData(String sinifAdi,String sehir, String  seviye, 
            String kullaniciSayisi, String egitmen, String kapasite, String taskName, int i) {
        
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(i);
        String[] data = new String[5];
            data[0] = sinifAdi;
            data[1] = sehir;
            data[2] = seviye+"-"+taskName;
            data[3] = kullaniciSayisi+"/"+kapasite;
            data[4] = egitmen;

        tableModel.addRow(data);
        jTable1.setModel(tableModel);
}
    
    public void getKisiselGorevlerListesiData() {
        getGorevlerFromXML(0,0); //ilk 0 if bloğundaki işleme girmemek için 0 verildi, ikinci 0 gereksiz fakan fonksiyon parametre beklediği için yazıldı
        DefaultTableModel tableModel = (DefaultTableModel) jTable2.getModel();
        String[] data = new String[4];
            data[0] = gorevId;
            data[1] = gorevAdi;
            data[2] = gorevIlgiliGorevler;
            if(Integer.parseInt(uyelerKisiselSeviye) < Integer.parseInt(gorevId)) {
                data[3] = "Tamamlanmadı";
            } else {
                data[3] = "Tamamlandı";
            }

        tableModel.addRow(data);
        jTable2.setModel(tableModel);
    }
    
    private void DosyaYukle() {
        FTPClient client = new FTPClient();
        FileInputStream fis = null;
      
            try {
                client.connect("ftp.anilozdem.com");
                client.login("anilozdem.com", "Kl60Ey91");

                //
                // Create an InputStream of the file to be uploaded
                //
                //String filename = "Touch.dat"; => Bu satıra gerek yok!
                fis = new FileInputStream(secilenDosyaninYolu);

                //
                // Store file to server
                //
                client.storeFile(secilenDosyaninYolu, fis);
                client.logout();
            } catch (IOException ex) {
                Logger.getLogger(OzgurCalis.class.getName()).log(Level.SEVERE, null, ex);
            }
            
         finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                client.disconnect();
            } catch (IOException e) {
                
            }
        }
            JOptionPane.showMessageDialog(null, "Dosya gönderme işlemi başarılı!");
    }
     

    

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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Execution problem!");
        }
     
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OzgurCalis().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel anaEkran;
    private javax.swing.JPanel anaEkranEgitmen;
    private javax.swing.JPanel anaEkranKisisel;
    private javax.swing.JButton anasayfa;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel destekKutuphanesi;
    private javax.swing.JPanel egitimlerEdit;
    private javax.swing.JButton egitmen;
    private javax.swing.JPanel gorevler;
    private javax.swing.JLabel gorevlerBg;
    private javax.swing.JPanel ilkEkran;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton62;
    private javax.swing.JButton jButton63;
    private javax.swing.JButton jButton64;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JPasswordField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JLabel kalanZamanBg;
    private javax.swing.JButton kisisel;
    private javax.swing.JButton kisiselKullanim;
    private javax.swing.JButton ogrenci;
    private javax.swing.JPanel sinifKontrol;
    private javax.swing.JPanel siniflar;
    private javax.swing.JPanel ustMenu;
    private javax.swing.JPanel uyeEkrani;
    private javax.swing.JPanel uyeEkraniKisisel;
    private javax.swing.JPanel uyeGirisi;
    private javax.swing.JPanel uyeOl;
    private javax.swing.ButtonGroup uyeOlRadioButton;
    private javax.swing.JButton yeniHesapOlustur;
    // End of variables declaration//GEN-END:variables
}
