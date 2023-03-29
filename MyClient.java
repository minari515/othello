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
	private JButton buttonArray[][];//ボタン用の配列
    private JButton passbtn;
    private JButton resetbtn;
    private int myColor;
    private int myTurn;
    private ImageIcon myIcon, yourIcon;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon, canIcon, resetIcon;
    private int passcount;
	PrintWriter out;//出力用のライター

	public MyClient() {
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}

        String IP =JOptionPane.showInputDialog(null,"IPアドレスを入力してください","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);
        if(IP.equals("")){
			IP = "localhost";//名前がないときは，"localhost"とする
		}
        

		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyClient");//ウィンドウのタイトルを設定する
		setSize(550,410);//ウィンドウのサイズを設定する
		c = getContentPane();//フレームのペインを取得する

		//アイコンの設定
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
        canIcon = new ImageIcon("GreenFrame2.jpg");
        resetIcon = new ImageIcon("resetbtn.jpg");

		c.setLayout(null);//自動レイアウトの設定を行わない
		//ボタンの生成
		buttonArray = new JButton[8][8];//ボタンの配列を５個作成する[0]から[4]まで使える
		for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                buttonArray[j][i] = new JButton(boardIcon);//ボタンにアイコンを設定する
                c.add(buttonArray[j][i]);//ペインに貼り付ける
                buttonArray[j][i].setBounds(i*45,j*45,45,45);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
                buttonArray[j][i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
                buttonArray[j][i].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
                buttonArray[j][i].setActionCommand(Integer.toString(j * 8 + i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
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
		
		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket(IP, 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
	}
		
	//メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続の最初に名前を送る
                String myNumberStr = br.readLine();
                int myNumberInt = Integer.parseInt(myNumberStr);
                
                if(myNumberInt % 2 == 0){
                    myColor = 0;
                    JButton sen = new JButton("先行");
                    add(sen);
                    sen.setBounds(460,10,60,30);
                    sen.setBackground(Color.WHITE);
                    JButton ban = new JButton("あなたの番です");
                    add(ban);
                    ban.setBounds(370,250,150,30);
                    ban.setBackground(Color.WHITE);
                }else {
                    myColor = 1;
                    JButton kou = new JButton("後攻");
                    add(kou);
                    kou.setBounds(460,10,60,30);
                    kou.setBackground(Color.WHITE);
                    JButton ban = new JButton("相手の番です");
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
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						if(cmd.equals("MOVE")){//cmdの文字と"MOVE"が同じか調べる．同じ時にtrueとなる
							//MOVEの時の処理(コマの移動の処理)
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int x = Integer.parseInt(inputTokens[2]);//数値に変換する
							int y = Integer.parseInt(inputTokens[3]);//数値に変換する
							buttonArray[theBnum][theBnum].setLocation(x,y);//指定のボタンを位置をx,yに設定する
						}else if(cmd.equals("PLACE")) {
                            String theBName = inputTokens[1];//ボタンの名前（番号）の取得
                            int theBnum = Integer.parseInt(theBName);
                            int theColor = Integer.parseInt(inputTokens[2]);
                            int x = theBnum % 8;//数値に変換する
							int y = theBnum / 8;//数値に変換する
                            if(theColor == myColor){
                                //送信元クライアントでの処理
                                buttonArray[y][x].setIcon(myIcon);
                                myTurn = 1 - myTurn; // 0を1に，1を0にする
                                JButton ban = new JButton("相手の番です");
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
                            } else {//送信先クライアントでの処理
                                buttonArray[y][x].setIcon(yourIcon);
                                myTurn = 1 - myTurn; // 0を1に，1を0にする
                                JButton ban = new JButton("あなたの番です");
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
                            System.out.println("ゲーム終了");
                            String msg3 = "FINISH";
                            out.println(msg3);
                            }
                        }else if(cmd.equals("FLIP")){
                                String theBName = inputTokens[1];//ボタンの名前（番号）の取得
                                int theBnum = Integer.parseInt(theBName);
                                int theColor = Integer.parseInt(inputTokens[2]);
                                int x = theBnum % 8;//数値に変換する
                                int y = theBnum / 8;//数値に変換する
                                if(theColor == myColor){
                                    //送信元クライアントでの処理
                                    buttonArray[y][x].setIcon(myIcon);
                                } else {//送信先クライアントでの処理
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
                                    System.out.println("あなたの勝利です");
                                }else{
                                    System.out.println("あなたの敗北です");
                                }
                            }else{
                                if(myColor == 0){
                                    System.out.println("あなたの敗北です");
                                }else{
                                    System.out.println("あなたの勝利です");
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
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
		System.out.println("クリック");
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
        System.out.println(theArrayIndex);

		Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る
		System.out.println(theIcon);//デバッグ（確認用）に，クリックしたアイコンの名前を出力する

		/*if(myColor == 0){//アイコンがwhiteIconと同じなら
			theButton.setIcon(blackIcon);//blackIconに設定する
		}else{
			theButton.setIcon(whiteIcon);//whiteIconに設定する
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
                if(judgeButton(b,a)){//置ける
                //送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
                    passcount =0;
                    String msg1 = "PLACE"+" "+theArrayIndex+" "+myColor;

                //サーバに情報を送る
                    out.println(msg1);//送信データをバッファに書き出

                    repaint();//画面のオブジェクトを描画し直す
                    if(judgeButton1()){
                        System.out.println("ゲーム終了");
                        String msg3 = "FINISH";
                        out.println(msg3);
                    }
                    out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
                }else {
                    System.out.println("そこには配置できません。");
                }
            }else if(myTurn == 1){
                //置けない
                System.out.println("あなたの番ではありません");
            }else if(myTurn == 2){
                System.out.println("ゲームは終了しました");
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
                            //ボタンの位置情報を作る
                            int msgy = b + dy;
                            int msgx = a + dx;
                            int theArrayIndex = msgy*8 + msgx;
  
                            //サーバに情報を送る
                            String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
                            out.println(msg);
                            out.flush();
                        }
                    }
                }
            }
        }
        //色々な条件からflagをtrueにするか判断する
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
        //色々な条件からflagをtrueにするか判断する
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
    
    public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
        //System.out.println("マウスが入った");
    }
	
    public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
        //System.out.println("マウス脱出");
    }
	
	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
		//System.out.println("マウスを押した");
	}
	
	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
		//System.out.println("マウスを放した");
	}
	
	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラッグしているときの処理
		/*System.out.println("マウスをドラッグ");
		JButton theButton = (JButton)e.getComponent();//型が違うのでキャストする
		String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す

		Point theMLoc = e.getPoint();//発生元コンポーネントを基準とする相対座標
		System.out.println(theMLoc);//デバッグ（確認用）に，取得したマウスの位置をコンソールに出力する
		Point theBtnLocation = theButton.getLocation();//クリックしたボタンを座標を取得する
		theBtnLocation.x += theMLoc.x-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
		theBtnLocation.y += theMLoc.y-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
        if (!(theArrayIndex.equals("0"))) {
            theButton.setLocation(theBtnLocation);//マウスの位置にあわせてオブジェクトを移動する
        
 
            //送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
            String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

            //サーバに情報を送る
            out.println(msg);//送信データをバッファに書き出す
            out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

            repaint();//オブジェクトの再描画を行う
        }*/
	}

	public void mouseMoved(MouseEvent e) {
		//System.out.println("マウス移動");
		int theMLocX = e.getX();//マウスのx座標を得る
		int theMLocY = e.getY();//マウスのy座標を得る
		//System.out.println(theMLocX+","+theMLocY);//コンソールに出力する
	}
}
