import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class MyClient extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonArray[][];//�{�^���p�̔z��
    private JButton passbtn;
    private JButton resetbtn;
    private int myColor;
    private int myTurn;
    private ImageIcon myIcon, yourIcon;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon, canIcon, resetIcon;
    private int passcount;
	PrintWriter out;//�o�͗p�̃��C�^�[

	public MyClient() {
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}

        String IP =JOptionPane.showInputDialog(null,"IP�A�h���X����͂��Ă�������","IP�A�h���X�̓���",JOptionPane.QUESTION_MESSAGE);
        if(IP.equals("")){
			IP = "localhost";//���O���Ȃ��Ƃ��́C"localhost"�Ƃ���
		}
        

		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyClient");//�E�B���h�E�̃^�C�g����ݒ肷��
		setSize(550,410);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();//�t���[���̃y�C�����擾����

		//�A�C�R���̐ݒ�
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
        canIcon = new ImageIcon("GreenFrame2.jpg");
        resetIcon = new ImageIcon("resetbtn.jpg");

		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		//�{�^���̐���
		buttonArray = new JButton[8][8];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
		for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                buttonArray[j][i] = new JButton(boardIcon);//�{�^���ɃA�C�R����ݒ肷��
                c.add(buttonArray[j][i]);//�y�C���ɓ\��t����
                buttonArray[j][i].setBounds(i*45,j*45,45,45);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
                buttonArray[j][i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
                buttonArray[j][i].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
                buttonArray[j][i].setActionCommand(Integer.toString(j * 8 + i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
            }
		}
        
        passbtn = new JButton("pass");
        add(passbtn);
        passbtn.setBounds(370,300,150,45);
        passbtn.addMouseListener(this);
        
        resetbtn = new JButton(resetIcon);
        c.add(resetbtn);
        resetbtn.setBounds(400,130,90,90);
        resetbtn.addMouseListener(this);
        
        
        buttonArray[3][3].setIcon(blackIcon);
        buttonArray[3][4].setIcon(whiteIcon);
        buttonArray[4][3].setIcon(whiteIcon);
        buttonArray[4][4].setIcon(blackIcon);
		
		//�T�[�o�ɐڑ�����
		Socket socket = null;
		try {
			//"localhost"�́C���������ւ̐ڑ��Dlocalhost��ڑ����IP Address�i"133.42.155.201"�`���j�ɐݒ肷��Ƒ���PC�̃T�[�o�ƒʐM�ł���
			//10000�̓|�[�g�ԍ��DIP Address�Őڑ�����PC�����߂āC�|�[�g�ԍ��ł���PC�㓮�삷��v���O��������肷��
			socket = new Socket(IP, 10000);
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g�� IP �A�h���X������ł��܂���: " + e);
		} catch (IOException e) {
			 System.err.println("�G���[���������܂���: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//��M�p�̃X���b�h���쐬����
		mrt.start();//�X���b�h�𓮂����iRun�������j
	}
		
	//���b�Z�[�W��M�̂��߂̃X���b�h
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//�ʐM�󋵂��Ď����C��M�f�[�^�ɂ���ē��삷��
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//�ڑ��̍ŏ��ɖ��O�𑗂�
                String myNumberStr = br.readLine();
                int myNumberInt = Integer.parseInt(myNumberStr);
                
                if(myNumberInt % 2 == 0){
                    myColor = 0;
                    JButton sen = new JButton("��s");
                    add(sen);
                    sen.setBounds(460,10,60,30);
                    sen.setBackground(Color.WHITE);
                    JButton ban = new JButton("���Ȃ��̔Ԃł�");
                    add(ban);
                    ban.setBounds(370,250,150,30);
                    ban.setBackground(Color.WHITE);
                }else {
                    myColor = 1;
                    JButton kou = new JButton("��U");
                    add(kou);
                    kou.setBounds(460,10,60,30);
                    kou.setBackground(Color.WHITE);
                    JButton ban = new JButton("����̔Ԃł�");
                    add(ban);
                    ban.setBounds(370,250,150,30);
                    ban.setBackground(Color.GRAY);
                }
                
                if(myColor == 0){
                    myIcon = blackIcon;
                    yourIcon = whiteIcon;
                    myTurn = 0;
                    for(int i = 0; i<8; i++){
                        for(int j=0; j<8; j++){
                            if(buttonArray[j][i].getIcon()==boardIcon){
                                if(judgeButton2(j,i)){
                                    buttonArray[j][i].setIcon(canIcon);
                                }
                            }
                        }
                    }
                }else {
                    myIcon = whiteIcon;
                    yourIcon = blackIcon;
                    myTurn = 1;
                }
                
				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
						if(cmd.equals("MOVE")){//cmd�̕�����"MOVE"�����������ׂ�D��������true�ƂȂ�
							//MOVE�̎��̏���(�R�}�̈ړ��̏���)
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int x = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
							int y = Integer.parseInt(inputTokens[3]);//���l�ɕϊ�����
							buttonArray[theBnum][theBnum].setLocation(x,y);//�w��̃{�^�����ʒu��x,y�ɐݒ肷��
						}else if(cmd.equals("PLACE")) {
                            String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
                            int theBnum = Integer.parseInt(theBName);
                            int theColor = Integer.parseInt(inputTokens[2]);
                            int x = theBnum % 8;//���l�ɕϊ�����
							int y = theBnum / 8;//���l�ɕϊ�����
                            if(theColor == myColor){
                                //���M���N���C�A���g�ł̏���
                                buttonArray[y][x].setIcon(myIcon);
                                myTurn = 1 - myTurn; // 0��1�ɁC1��0�ɂ���
                                JButton ban = new JButton("����̔Ԃł�");
                                add(ban);
                                ban.setBounds(370,250,150,30);
                                ban.setBackground(Color.GRAY);
                                for(int i = 0; i<8; i++){
                                    for(int j=0; j<8; j++){
                                        if(buttonArray[j][i].getIcon()==canIcon){
                                            buttonArray[j][i].setIcon(boardIcon);
                                        }
                                    }
                                }
                            } else {//���M��N���C�A���g�ł̏���
                                buttonArray[y][x].setIcon(yourIcon);
                                myTurn = 1 - myTurn; // 0��1�ɁC1��0�ɂ���
                                JButton ban = new JButton("���Ȃ��̔Ԃł�");
                                add(ban);
                                ban.setBounds(370,250,150,30);
                                ban.setBackground(Color.WHITE);
                                for(int i = 0; i<8; i++){
                                    for(int j=0; j<8; j++){
                                        if(buttonArray[j][i].getIcon()==boardIcon){
                                            if(judgeButton2(j,i)){
                                                buttonArray[j][i].setIcon(canIcon);
                                            }
                                        }
                                    }
                                }
                            }
                            if(judgeButton1()){
                            System.out.println("�Q�[���I��");
                            String msg3 = "FINISH";
                            out.println(msg3);
                            }
                        }else if(cmd.equals("FLIP")){
                                String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
                                int theBnum = Integer.parseInt(theBName);
                                int theColor = Integer.parseInt(inputTokens[2]);
                                int x = theBnum % 8;//���l�ɕϊ�����
                                int y = theBnum / 8;//���l�ɕϊ�����
                                if(theColor == myColor){
                                    //���M���N���C�A���g�ł̏���
                                    buttonArray[y][x].setIcon(myIcon);
                                } else {//���M��N���C�A���g�ł̏���
                                    buttonArray[y][x].setIcon(yourIcon);
                                }
                        }else if(cmd.equals("PASS")){
                                myTurn = 1 - myTurn;
                        }else if(cmd.equals("FINISH")){
                            myTurn = 2;
                            int senkou = 0;
                            int koukou = 0;
                            for(int i = 0; i<8; i++){
                                for(int j=0; j<8; j++){
                                    if(buttonArray[j][i].getIcon()==blackIcon){
                                        senkou++;
                                    }else if(buttonArray[j][i].getIcon()==whiteIcon){
                                        koukou++;
                                    }
                                }
                            }
                            if(senkou>koukou){
                                if(myColor == 0){
                                    System.out.println("���Ȃ��̏����ł�");
                                }else{
                                    System.out.println("���Ȃ��̔s�k�ł�");
                                }
                            }else{
                                if(myColor == 0){
                                    System.out.println("���Ȃ��̔s�k�ł�");
                                }else{
                                    System.out.println("���Ȃ��̏����ł�");
                                }
                            }
                        }else if(cmd.equals("RESET")){
                            for(int i=0;i<8;i++){
                                for(int j=0;j<8;j++){
                                    buttonArray[j][i].setIcon(boardIcon);
                                }
                            }
                            buttonArray[3][3].setIcon(blackIcon);
                            buttonArray[3][4].setIcon(whiteIcon);
                            buttonArray[4][3].setIcon(whiteIcon);
                            buttonArray[4][4].setIcon(blackIcon);
                        }
					}else{
						break;
					}
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("�G���[���������܂���: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {//�{�^�����N���b�N�����Ƃ��̏���
		System.out.println("�N���b�N");
		JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
		String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
        System.out.println(theArrayIndex);

		Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������
		System.out.println(theIcon);//�f�o�b�O�i�m�F�p�j�ɁC�N���b�N�����A�C�R���̖��O���o�͂���

		/*if(myColor == 0){//�A�C�R����whiteIcon�Ɠ����Ȃ�
			theButton.setIcon(blackIcon);//blackIcon�ɐݒ肷��
		}else{
			theButton.setIcon(whiteIcon);//whiteIcon�ɐݒ肷��
		}*/
        
        if(theButton == passbtn){
            System.out.println("pass");
            passcount++;
            if(passcount == 2){
                String msg3 = "FINISH";
                out.println(msg3);
                out.flush();
            }else{
            String msg2 = "PASS"+" "+passcount;
            out.println(msg2);
            out.flush();
            }
        }
        
        if(theButton == resetbtn){
            System.out.println("reset");
            String msg = "RESET";
            out.println(msg);
            out.flush();
        }
        
        if(theIcon == boardIcon || theIcon == canIcon) {
            int temp = Integer.parseInt(theArrayIndex);
            int a = temp%8;
            int b = temp/8;
            if (myTurn == 0) {
                if(judgeButton(b,a)){//�u����
                //���M�����쐬����i��M���ɂ́C���̑��������ԂɃf�[�^�����o���D�X�y�[�X���f�[�^�̋�؂�ƂȂ�j
                    passcount =0;
                    String msg1 = "PLACE"+" "+theArrayIndex+" "+myColor;

                //�T�[�o�ɏ��𑗂�
                    out.println(msg1);//���M�f�[�^���o�b�t�@�ɏ����o

                    repaint();//��ʂ̃I�u�W�F�N�g��`�悵����
                    if(judgeButton1()){
                        System.out.println("�Q�[���I��");
                        String msg3 = "FINISH";
                        out.println(msg3);
                    }
                    out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
                }else {
                    System.out.println("�����ɂ͔z�u�ł��܂���B");
                }
            }else if(myTurn == 1){
                //�u���Ȃ�
                System.out.println("���Ȃ��̔Ԃł͂���܂���");
            }else if(myTurn == 2){
                System.out.println("�Q�[���͏I�����܂���");
            }
        }
    }
	
    public boolean judgeButton(int b, int a){
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        
        boolean flag = false;
        for(int i=-1; i<=1;i++){
            for(int j=-1; j<=1;j++){
                if((b+j)<8&&(b+j)>=0&&(a+i)<8&&(a+i)>=0){
                    if(flipButtons(b,a,j,i)>0){
                        flag = true;
                        for(int dy=j, dx=i, k=0; k<flipButtons(b,a,j,i); k++, dy+=j, dx+=i){
                            if(dx==0&&dy==0){
                                break;
                            }
                            passcount = 0;
                            //�{�^���̈ʒu�������
                            int msgy = b + dy;
                            int msgx = a + dx;
                            int theArrayIndex = msgy*8 + msgx;
  
                            //�T�[�o�ɏ��𑗂�
                            String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
                            out.println(msg);
                            out.flush();
                        }
                    }
                }
            }
        }
        //�F�X�ȏ�������flag��true�ɂ��邩���f����
    return flag;
    }
    
    public boolean judgeButton2(int b, int a){
        
        boolean flag = false;
        for(int i=-1; i<=1;i++){
            for(int j=-1; j<=1;j++){
                if((b+j)<8&&(b+j)>=0&&(a+i)<8&&(a+i)>=0){
                    if(flipButtons(b,a,j,i)>0){
                        flag = true;
                    }
                }
            }
        }
        //�F�X�ȏ�������flag��true�ɂ��邩���f����
    return flag;
    }
    
    public int flipButtons(int b, int a, int j, int i){
        int flipNum = 0;
        for(int dy=j, dx=i; ; dy+=j, dx+=i) {
            if(b+dy>7||b+dy<0||a+dx>7||a+dx<0){
                return 0;
            }
            
            Icon nowIcon = buttonArray[b+dy][a+dx].getIcon();
            if(nowIcon==boardIcon || nowIcon==canIcon){
                return 0;
            }else if(nowIcon==myIcon){
                return flipNum;
            }else if(nowIcon==yourIcon){
                flipNum++;
            }
        }
    }
    
    public boolean judgeButton1(){
        boolean flag = true;
        for(int i = 0; i<8; i++){
            for(int j=0; j<8; j++){
                if(buttonArray[j][i].getIcon()==boardIcon||buttonArray[j][i].getIcon()==canIcon){
                    flag = false;
                }
            }
        }
        return flag;
    }
    
    public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
        //System.out.println("�}�E�X��������");
    }
	
    public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
        //System.out.println("�}�E�X�E�o");
    }
	
	public void mousePressed(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏����i�N���b�N�Ƃ̈Ⴂ�ɒ��Ӂj
		//System.out.println("�}�E�X��������");
	}
	
	public void mouseReleased(MouseEvent e) {//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���
		//System.out.println("�}�E�X�������");
	}
	
	public void mouseDragged(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g�Ƃ��h���b�O���Ă���Ƃ��̏���
		/*System.out.println("�}�E�X���h���b�O");
		JButton theButton = (JButton)e.getComponent();//�^���Ⴄ�̂ŃL���X�g����
		String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��

		Point theMLoc = e.getPoint();//�������R���|�[�l���g����Ƃ��鑊�΍��W
		System.out.println(theMLoc);//�f�o�b�O�i�m�F�p�j�ɁC�擾�����}�E�X�̈ʒu���R���\�[���ɏo�͂���
		Point theBtnLocation = theButton.getLocation();//�N���b�N�����{�^�������W���擾����
		theBtnLocation.x += theMLoc.x-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
		theBtnLocation.y += theMLoc.y-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
        if (!(theArrayIndex.equals("0"))) {
            theButton.setLocation(theBtnLocation);//�}�E�X�̈ʒu�ɂ��킹�ăI�u�W�F�N�g���ړ�����
        
 
            //���M�����쐬����i��M���ɂ́C���̑��������ԂɃf�[�^�����o���D�X�y�[�X���f�[�^�̋�؂�ƂȂ�j
            String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

            //�T�[�o�ɏ��𑗂�
            out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
            out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����

            repaint();//�I�u�W�F�N�g�̍ĕ`����s��
        }*/
	}

	public void mouseMoved(MouseEvent e) {
		//System.out.println("�}�E�X�ړ�");
		int theMLocX = e.getX();//�}�E�X��x���W�𓾂�
		int theMLocY = e.getY();//�}�E�X��y���W�𓾂�
		//System.out.println(theMLocX+","+theMLocY);//�R���\�[���ɏo�͂���
	}
}
