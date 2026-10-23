import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.prefs.Preferences;

public class Main extends JFrame {
    private static final Color BG = new Color(248,245,239), CARD = Color.WHITE, INK = new Color(42,32,27), MUTED = new Color(108,94,84), ACCENT = new Color(167,73,45), LIGHT = new Color(244,235,225), GREEN = new Color(42,121,92), RED = new Color(176,61,56);
    private static final Font SERIF = new Font("Georgia", Font.BOLD, 30), TITLE = new Font("Georgia", Font.BOLD, 22), BODY = new Font("Segoe UI", Font.PLAIN, 14), SMALL = new Font("Segoe UI", Font.PLAIN, 12), BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final Map<String,User> users = new LinkedHashMap<>();
    private final Preferences userStore = Preferences.userRoot().node("ChefOnCall/users");
    private User currentUser;
    private final Map<String, ImageIcon> imageCache = new HashMap<>();
    private Chef selectedChef;
    private boolean showOrderChoiceOnChefSelection = false;
    private boolean immediate;
    private boolean availabilityChecked;
    private JComboBox<String> cuisineBox, preferenceBox, guestsBox, timeBox;
    private JComboBox<Chef> chefBox;
    private JTextField dateField, nameField, phoneField, addressField, requestField;
    private JLabel chefPhoto, chefName, chefMeta, chefSpeciality, chefDish, chefPrice, availabilityLabel, totalLabel, immediateLabel;
    private JSpinner guestSpinner;
    private JPanel chefCard, bookingsPanel;
    private DishCarouselPanel loginDishPanel;

    public Main(){
        setTitle("ChefOnCall — Your Chef, Your Cuisine"); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setSize(1220,800); setMinimumSize(new Dimension(1050,700)); setLocationRelativeTo(null); loadUsers(); showLogin();
    }
    public static void main(String[] args){ SwingUtilities.invokeLater(() -> new Main().setVisible(true)); }

    private void baseFrame(){
        stopLoginDishAnimation();
        getContentPane().removeAll();
        getContentPane().setBackground(BG);
    }

    private void stopLoginDishAnimation(){
        if(loginDishPanel!=null){
            loginDishPanel.stop();
            loginDishPanel=null;
        }
    }
    private JPanel root(){ JPanel p=new JPanel(new BorderLayout(0,0)); p.setBackground(BG); p.setBorder(new EmptyBorder(22,28,22,28)); return p; }
    private JLabel label(String s){ JLabel l=new JLabel(s); l.setFont(BOLD); l.setForeground(MUTED); return l; }
    private JLabel hint(String s){ JLabel l=new JLabel("<html>"+s+"</html>"); l.setFont(SMALL); l.setForeground(MUTED); return l; }
    private JTextField field(String placeholder){ JTextField f=new JTextField(); f.setFont(BODY); f.setPreferredSize(new Dimension(10,40)); f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(215,204,193)), new EmptyBorder(7,10,7,10))); return f; }
    private JComboBox<String> combo(String... vals){ JComboBox<String> c=new JComboBox<>(vals); c.setFont(BODY); c.setPreferredSize(new Dimension(10,40)); return c; }
    private JButton button(String text, boolean primary){ JButton b=new JButton(text); b.setFont(BOLD); b.setForeground(primary?Color.WHITE:INK); b.setBackground(primary?ACCENT:Color.WHITE); b.setFocusPainted(false); b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(primary?ACCENT:new Color(215,204,193)), new EmptyBorder(10,18,10,18))); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b; }

    private void showLogin(){
        baseFrame();
        JPanel r = root();

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(new Color(45,34,29));
        left.setBorder(new EmptyBorder(50,50,50,50));
        JLabel brand = new JLabel("ChefOnCall");
        brand.setFont(new Font("Georgia",Font.BOLD,44));
        brand.setForeground(new Color(246,224,205));
        JLabel sub = new JLabel("PRIVATE CHEF APPOINTMENTS");
        sub.setFont(BOLD);
        sub.setForeground(new Color(221,190,165));
        JPanel leftTop = new JPanel();
        leftTop.setOpaque(false);
        leftTop.setLayout(new BoxLayout(leftTop, BoxLayout.Y_AXIS));
        leftTop.add(brand);
        leftTop.add(Box.createVerticalStrut(6));
        leftTop.add(sub);

        loginDishPanel = new DishCarouselPanel();
        loginDishPanel.setPreferredSize(new Dimension(420, 380));

        JTextArea quote = new JTextArea("A chef for your cuisine,\na menu made around you,\nand a meal worth remembering.");
        quote.setFont(new Font("Georgia",Font.PLAIN,23));
        quote.setForeground(Color.WHITE);
        quote.setOpaque(false);
        quote.setEditable(false);
        quote.setLineWrap(false);
        quote.setBorder(new EmptyBorder(12,0,0,0));

        left.add(leftTop,BorderLayout.NORTH);
        left.add(loginDishPanel,BorderLayout.CENTER);
        left.add(quote,BorderLayout.SOUTH);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(CARD);
        right.setBorder(new EmptyBorder(35,55,35,55));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.gridy=0; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        g.insets=new Insets(7,0,7,0);

        JLabel h = new JLabel("Welcome back");
        h.setFont(SERIF); h.setForeground(INK); right.add(h,g);
        g.gridy++;
        JLabel t = new JLabel("Sign in with the email and 6-digit password used when you registered.");
        t.setFont(BODY); t.setForeground(MUTED); right.add(t,g);

        g.gridy++;
        JTextField email = field("");
        email.setToolTipText("Example: name@gmail.com");
        right.add(formBlock("EMAIL", email, "Use the same email you used during registration."),g);

        g.gridy++;
        JPasswordField pass = passwordField();
        right.add(formBlock("PASSWORD", passwordWithEye(pass), "Exactly 6 digits. Numbers only."),g);

        g.gridy++;
        JLabel status = new JLabel(" ");
        status.setFont(SMALL);
        status.setForeground(RED);
        right.add(status,g);

        g.gridy++;
        JButton login = button("SIGN IN",true);
        login.setDefaultCapable(true);
        right.add(login,g);

        g.gridy++;
        JButton reg = button("CREATE NEW ACCOUNT",false);
        right.add(reg,g);

        g.gridy++;
        JButton forgot = new JButton("Forgot password?");
        forgot.setBorderPainted(false);
        forgot.setContentAreaFilled(false);
        forgot.setForeground(ACCENT);
        forgot.setFont(BOLD);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        right.add(forgot,g);

        // Keep login input within the rules while the user types.
        pass.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ limitDigits(pass,6); }
            public void removeUpdate(DocumentEvent e) { }
            public void changedUpdate(DocumentEvent e) { }
        });

        Runnable doLogin = () -> {
            String em = email.getText().trim().toLowerCase();
            String pw = new String(pass.getPassword());

            if(em.isEmpty()){
                status.setText("Please enter your email address.");
                email.requestFocusInWindow();
                return;
            }
            if(!Validation.validEmail(em)){
                status.setText("Enter a valid email with an extension such as .com or .in.");
                email.requestFocusInWindow();
                return;
            }
            if(pw.isEmpty()){
                status.setText("Please enter your 6-digit password.");
                pass.requestFocusInWindow();
                return;
            }
            if(!Validation.validPassword(pw)){
                status.setText("Password must contain exactly 6 digits — numbers only.");
                pass.requestFocusInWindow();
                return;
            }

            User u = users.get(em);
            if(u == null){
                status.setText("Account not found. Please create an account first.");
                return;
            }
            if(!u.getPassword().equals(pw)){
                status.setText("Incorrect password. Try again or use Forgot password.");
                pass.requestFocusInWindow();
                pass.selectAll();
                return;
            }

            status.setForeground(GREEN);
            status.setText("Signed in successfully. Opening ChefOnCall...");
            currentUser = u;
            // Switch screens immediately. Chef photos are loaded in the background
            // so a slow/no internet connection can never freeze the Swing UI.
            SwingUtilities.invokeLater(this::showHome);
        };

        login.addActionListener(e -> doLogin.run());
        email.addActionListener(e -> doLogin.run());
        pass.addActionListener(e -> doLogin.run());
        reg.addActionListener(e -> showRegister());
        forgot.addActionListener(e -> showForgot());
        getRootPane().setDefaultButton(login);

        r.add(left,BorderLayout.WEST);
        left.setPreferredSize(new Dimension(440,0));
        r.add(right,BorderLayout.CENTER);
        add(r);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(() -> email.requestFocusInWindow());
    }

    private void loadUsers(){
        try{
            for(String key : userStore.keys()){
                String data = userStore.get(key, null);
                if(data == null) continue;
                String[] parts = data.split("\\|", -1);
                if(parts.length == 4){
                    users.put(parts[1].toLowerCase(), new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }catch(Exception ignored){}
    }

    private void saveUser(User u){
        try{
            String key = Integer.toHexString(u.getEmail().hashCode());
            userStore.put(key, u.getName()+"|"+u.getEmail()+"|"+u.getPhone()+"|"+u.getPassword());
            userStore.flush();
        }catch(Exception ignored){}
    }

    private JPanel formBlock(String title, JComponent comp, String help){ JPanel p=new JPanel(new BorderLayout(0,4)); p.setOpaque(false); p.add(label(title),BorderLayout.NORTH); p.add(comp,BorderLayout.CENTER); p.add(hint(help),BorderLayout.SOUTH); return p; }

    private void showRegister(){
        baseFrame();
        JPanel r = root();
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(34, 55, 34, 55));

        JLabel h = new JLabel("Create your ChefOnCall account");
        h.setFont(SERIF);
        h.setForeground(INK);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(h);
        card.add(Box.createVerticalStrut(8));

        JLabel instructions = hint("<b>Account rules:</b> Full name = first + last name &nbsp;•&nbsp; Email = valid address with a recognised extension &nbsp;•&nbsp; Phone = exactly 10 digits &nbsp;•&nbsp; Password = exactly 6 digits, numbers only.");
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(instructions);
        card.add(Box.createVerticalStrut(18));

        JTextField name = field("");
        JTextField email = field("");
        JTextField phone = field("");
        JPasswordField p1 = passwordField();
        JPasswordField p2 = passwordField();

        card.add(formBlock("FULL NAME", name, "Enter your first and last name."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("EMAIL", email, "Example: khushi@gmail.com • Allowed extensions: .com, .in, .org, .net, .edu, .gov, .co, .biz, .info, .me, .io, .ai"));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("PHONE NUMBER", phone, "Exactly 10 digits. Do not enter +91, spaces or symbols."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("PASSWORD", passwordWithEye(p1), "Exactly 6 digits — numbers only. No letters or special characters."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("CONFIRM PASSWORD", passwordWithEye(p2), "Repeat the same 6-digit password."));
        card.add(Box.createVerticalStrut(18));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton create = button("CREATE ACCOUNT", true);
        JButton back = button("BACK TO LOGIN", false);
        row.add(create);
        row.add(back);
        card.add(row);

        phone.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ limitDigits(phone); }
            public void removeUpdate(DocumentEvent e){}
            public void changedUpdate(DocumentEvent e){}
        });

        create.addActionListener(e -> {
            String n = name.getText().trim();
            String em = email.getText().trim().toLowerCase();
            String ph = phone.getText().trim();
            String pw = new String(p1.getPassword());
            String cpw = new String(p2.getPassword());

            if(!Validation.validName(n)){ msg("Please enter your full name — first name and last name are required.", true); return; }
            if(!Validation.validEmail(em)){ msg("Please enter a valid email such as name@gmail.com with a recognised mail extension.", true); return; }
            if(!Validation.validPhone(ph)){ msg("Phone number must contain exactly 10 digits.", true); return; }
            if(!Validation.validPassword(pw)){ msg("Password must be exactly 6 digits and numbers only. Example: 482915", true); return; }
            if(!pw.equals(cpw)){ msg("Passwords do not match.", true); return; }
            if(users.containsKey(em)){ msg("An account with this email already exists.", true); return; }

            User newUser = new User(n, em, ph, pw);
            users.put(em, newUser);
            saveUser(newUser);
            msg("Account created successfully. You can now sign in.", false);
            showLogin();
        });
        back.addActionListener(e -> showLogin());

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        r.add(scroll, BorderLayout.CENTER);
        add(r);
        revalidate();
        repaint();
    }

    private JPasswordField passwordField(){
        JPasswordField p = new JPasswordField();
        p.setFont(BODY);
        p.setPreferredSize(new Dimension(10,40));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215,204,193)),
                new EmptyBorder(7,10,7,10)));
        return p;
    }

    private JPanel passwordWithEye(JPasswordField password){
        JPanel panel = new JPanel(new BorderLayout(6,0));
        panel.setOpaque(false);

        JButton eye = new JButton("👁");
        eye.setToolTipText("Show password");
        eye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        eye.setPreferredSize(new Dimension(48,40));
        eye.setFocusPainted(false);
        eye.setContentAreaFilled(true);
        eye.setBackground(Color.WHITE);
        eye.setBorder(BorderFactory.createLineBorder(new Color(215,204,193)));
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final char mask = password.getEchoChar();
        eye.addActionListener(e -> {
            if(password.getEchoChar() == 0){
                password.setEchoChar(mask == 0 ? '•' : mask);
                eye.setToolTipText("Show password");
            } else {
                password.setEchoChar((char)0);
                eye.setToolTipText("Hide password");
            }
            password.requestFocusInWindow();
        });

        panel.add(password, BorderLayout.CENTER);
        panel.add(eye, BorderLayout.EAST);
        return panel;
    }

    private void showForgot(){
        baseFrame();
        JPanel r = root();

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(50, 70, 50, 70));

        JLabel h = new JLabel("Reset your password");
        h.setFont(SERIF);
        h.setForeground(INK);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(h);
        card.add(Box.createVerticalStrut(10));

        JLabel instructions = hint("Enter your registered email and choose a new 6-digit password.");
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(instructions);
        card.add(Box.createVerticalStrut(22));

        JTextField email = field("");
        JPasswordField p1 = passwordField();
        JPasswordField p2 = passwordField();

        card.add(formBlock("EMAIL", email, "Use the email you registered with."));
        card.add(Box.createVerticalStrut(12));
        card.add(formBlock("NEW PASSWORD", passwordWithEye(p1), "Exactly 6 digits — numbers only."));
        card.add(Box.createVerticalStrut(12));
        card.add(formBlock("CONFIRM PASSWORD", passwordWithEye(p2), "Repeat the same 6 digits."));
        card.add(Box.createVerticalStrut(22));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton reset = button("RESET PASSWORD", true);
        JButton back = button("BACK", false);
        row.add(reset);
        row.add(back);
        card.add(row);

        DocumentListener digits = new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ limitDigits(p1, 6); limitDigits(p2, 6); }
            public void removeUpdate(DocumentEvent e){}
            public void changedUpdate(DocumentEvent e){}
        };
        p1.getDocument().addDocumentListener(digits);
        p2.getDocument().addDocumentListener(digits);

        reset.addActionListener(e -> {
            String em = email.getText().trim().toLowerCase();
            String pw = new String(p1.getPassword());
            String cpw = new String(p2.getPassword());

            if(!Validation.validEmail(em) || !users.containsKey(em)){
                msg("Enter the email of an existing account.", true);
                return;
            }
            if(!Validation.validPassword(pw) || !pw.equals(cpw)){
                msg("New password must be exactly 6 digits and both fields must match.", true);
                return;
            }

            users.get(em).setPassword(pw);
            saveUser(users.get(em));
            msg("Password reset successfully.", false);
            showLogin();
        });
        back.addActionListener(e -> showLogin());

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        r.add(scroll, BorderLayout.CENTER);
        add(r);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(() -> email.requestFocusInWindow());
    }

    private void showHome(){
        baseFrame();
        JPanel r = root();

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(42, 60, 42, 60));

        JLabel h = new JLabel("How would you like to enjoy ChefOnCall?");
        h.setFont(SERIF); h.setForeground(INK); h.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(h);
        card.add(Box.createVerticalStrut(8));

        JLabel info = hint("Choose one service to continue. You can select your cuisine and chef on the Call Chef at Home page.");
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(info);
        card.add(Box.createVerticalStrut(28));

        JPanel choices = new JPanel(new GridLayout(1, 2, 18, 0));
        choices.setOpaque(false);
        choices.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel chefChoice = serviceChoiceCard(
                "CALL CHEF AT HOME",
                "Choose your cuisine and chef, then enter your appointment details.",
                "CHOOSE A CHEF");
        JPanel tiffinChoice = serviceChoiceCard(
                "TIFFIN & DELIVERY",
                "Choose your cuisine, meal preference, number of tiffins and delivery service.",
                "ORDER TIFFIN");
        choices.add(chefChoice); choices.add(tiffinChoice);
        card.add(choices);
        card.add(Box.createVerticalStrut(24));

        JLabel note = hint("Your bookings will be saved under My Bookings after confirmation.");
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(note);

        JButton logout = button("LOG OUT", false);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(22));
        card.add(logout);

        ((JButton)chefChoice.getClientProperty("actionButton")).addActionListener(e -> showChefPage());
        ((JButton)tiffinChoice.getClientProperty("actionButton")).addActionListener(e -> showTiffin());
        logout.addActionListener(e -> { currentUser=null; showLogin(); });

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        r.add(scroll, BorderLayout.CENTER);
        add(r); revalidate(); repaint();
    }

    private JPanel serviceChoiceCard(String title, String description, String buttonText){
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224,214,204)),
                new EmptyBorder(24,24,24,24)));
        JLabel h = new JLabel(title); h.setFont(TITLE); h.setForeground(INK); h.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(h); p.add(Box.createVerticalStrut(12));
        JLabel d = hint(description); d.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(d);
        p.add(Box.createVerticalStrut(22));
        JButton b = button(buttonText, true); b.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(b);
        p.putClientProperty("actionButton", b);
        return p;
    }

    private void showChefPage(){
        baseFrame(); JPanel root=root(); JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);JPanel brand=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));brand.setOpaque(false);JLabel b=new JLabel("ChefOnCall");b.setFont(SERIF);b.setForeground(ACCENT);brand.add(b);JLabel s=new JLabel("  PRIVATE CHEF APPOINTMENTS");s.setFont(BOLD);s.setForeground(MUTED);brand.add(s);top.add(brand,BorderLayout.WEST);JPanel nav=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));nav.setOpaque(false);JButton bookings=button("MY BOOKINGS",false), tiffinBtn=button("TIFFIN & DELIVERY",false), logout=button("LOG OUT",false);nav.add(new JLabel("Hi, "+currentUser.getName().split(" ")[0]+"   "));nav.add(bookings);nav.add(tiffinBtn);nav.add(logout);top.add(nav,BorderLayout.EAST);root.add(top,BorderLayout.NORTH);
        JPanel content=new JPanel(new GridLayout(1,2,20,0));content.setOpaque(false);content.setBorder(new EmptyBorder(22,0,0,0));content.add(buildChoosePanel());content.add(buildBookingPanel());root.add(content,BorderLayout.CENTER);add(root);revalidate();repaint(); updateChef();
        bookings.addActionListener(e->showBookings());tiffinBtn.addActionListener(e->showTiffin());logout.addActionListener(e->{currentUser=null;showLogin();});
    }

    private JPanel buildChoosePanel(){
        JPanel p=cardPanel();JLabel h=new JLabel("Find your chef");h.setFont(SERIF);h.setForeground(INK);p.add(h,BorderLayout.NORTH);JPanel form=new JPanel();form.setOpaque(false);form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        cuisineBox=combo(ChefData.CHEFS.keySet().toArray(new String[0])); chefBox=new JComboBox<>(); chefBox.setFont(BODY); chefBox.setPreferredSize(new Dimension(10,40)); preferenceBox=combo("Vegetarian","Non-Vegetarian","Eggitarian");guestsBox=combo("1","2","3","4","5","6","7","8","9","10");timeBox=combo("8:00 AM","10:00 AM","11:00 AM","12:00 PM","1:00 PM","3:00 PM","5:00 PM","7:00 PM","9:00 PM");dateField=field("");dateField.setText(LocalDate.now().format(DATE_FMT));
        form.add(fieldBlock("CUISINE",cuisineBox,"Indian cuisines first, plus international favourites."));form.add(Box.createVerticalStrut(10));form.add(fieldBlock("CHOOSE YOUR CHEF",chefBox,"Compare chefs by speciality, dish experience, rating and price."));form.add(Box.createVerticalStrut(10));form.add(fieldBlock("FOOD PREFERENCE",preferenceBox,"Choose what you want the chef to prepare."));form.add(Box.createVerticalStrut(10));form.add(fieldBlock("NUMBER OF GUESTS",guestsBox,"Price is calculated per person, per meal."));form.add(Box.createVerticalStrut(10));form.add(fieldBlock("APPOINTMENT DATE",dateField,"Format: DD-MM-YYYY. Date cannot be in the past."));form.add(Box.createVerticalStrut(10));form.add(fieldBlock("TIME",timeBox,"Choose a preferred meal time."));form.add(Box.createVerticalStrut(12));
        JPanel immediatePanel=new JPanel(new BorderLayout(8,0));immediatePanel.setBackground(LIGHT);immediatePanel.setBorder(new EmptyBorder(10,12,10,12));JCheckBox now=new JCheckBox("I need a chef within 2 hours");now.setOpaque(false);now.setFont(BOLD);now.setForeground(INK);immediateLabel=new JLabel("+30% priority fee");immediateLabel.setForeground(ACCENT);immediateLabel.setFont(SMALL);immediatePanel.add(now,BorderLayout.CENTER);immediatePanel.add(immediateLabel,BorderLayout.EAST);form.add(immediatePanel);now.addActionListener(e->{immediate=now.isSelected();updateChef();});
        form.add(Box.createVerticalStrut(14));chefCard=new JPanel(new BorderLayout(14,0));chefCard.setBackground(new Color(251,247,242));chefCard.setBorder(new EmptyBorder(14,14,14,14));form.add(chefCard);
        form.add(Box.createVerticalStrut(14));
        p.add(form,BorderLayout.CENTER);
        cuisineBox.addActionListener(e->{availabilityChecked=false; populateChefBox();});
        chefBox.addActionListener(e->{availabilityChecked=false; updateChef();});
        guestsBox.addActionListener(e->updateChef());
        showOrderChoiceOnChefSelection=false;
        populateChefBox();
        return p;
    }

    private void showOrderChoice(){
        if(selectedChef==null) return;
        JPanel panel=new JPanel(new BorderLayout(0,14));
        panel.setBorder(new EmptyBorder(10,10,5,10));
        panel.setBackground(CARD);
        JLabel title=new JLabel("What would you like to do?");
        title.setFont(SERIF); title.setForeground(INK);
        JLabel info=new JLabel("<html><b>"+selectedChef.getName()+"</b> selected for "+selectedChef.getCuisine()+".<br>Choose how you want to continue:</html>");
        info.setFont(BODY); info.setForeground(MUTED);
        JPanel buttons=new JPanel(new GridLayout(1,2,12,0)); buttons.setOpaque(false);
        JButton appointment=button("BOOK AN APPOINTMENT",true);
        JButton tiffin=button("TIFFIN DELIVERY",false);
        buttons.add(appointment); buttons.add(tiffin);
        panel.add(title,BorderLayout.NORTH);
        panel.add(info,BorderLayout.CENTER);
        panel.add(buttons,BorderLayout.SOUTH);
        JDialog dialog=new JDialog(this,"Choose your ChefOnCall service",true);
        dialog.setContentPane(panel); dialog.setSize(620,220); dialog.setLocationRelativeTo(this);
        appointment.addActionListener(e->{dialog.dispose(); SwingUtilities.invokeLater(()->{if(nameField!=null) nameField.requestFocusInWindow();});});
        tiffin.addActionListener(e->{dialog.dispose(); showTiffin();});
        dialog.setVisible(true);
    }

    private JPanel fieldBlock(String title,JComponent comp,String help){JPanel p=new JPanel(new BorderLayout(0,4));p.setOpaque(false);p.add(label(title),BorderLayout.NORTH);p.add(comp,BorderLayout.CENTER);p.add(hint(help),BorderLayout.SOUTH);return p;}
    private JPanel cardPanel(){JPanel p=new JPanel(new BorderLayout(0,14));p.setBackground(CARD);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(224,214,204)),new EmptyBorder(22,24,22,24)));return p;}

    private JPanel buildBookingPanel(){
        JPanel p=cardPanel();JLabel h=new JLabel("Book your appointment");h.setFont(SERIF);h.setForeground(INK);p.add(h,BorderLayout.NORTH);JPanel form=new JPanel();form.setOpaque(false);form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        nameField=field("");phoneField=field("");addressField=field("");requestField=field("");
        phoneField.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){limitDigits();}public void removeUpdate(DocumentEvent e){}public void changedUpdate(DocumentEvent e){}});
        form.add(fieldBlock("FULL NAME",nameField,"Must contain at least first name + last name."));form.add(Box.createVerticalStrut(9));form.add(fieldBlock("PHONE NUMBER",phoneField,"Exactly 10 digits. No +91, spaces or symbols."));form.add(Box.createVerticalStrut(9));form.add(fieldBlock("FULL ADDRESS",addressField,"House/flat no., area, city, state and 6-digit pincode. Separate parts with commas."));form.add(Box.createVerticalStrut(9));form.add(fieldBlock("SPECIAL REQUESTS (OPTIONAL)",requestField,"Allergies, preferred spice level, dishes you want, etc."));form.add(Box.createVerticalStrut(12));
        JButton check=button("CHECK CHEF AVAILABILITY",false);availabilityLabel=new JLabel("Not checked yet");availabilityLabel.setFont(BOLD);availabilityLabel.setForeground(MUTED);JPanel ar=new JPanel(new BorderLayout(10,0));ar.setOpaque(false);ar.add(check,BorderLayout.WEST);ar.add(availabilityLabel,BorderLayout.CENTER);form.add(ar);form.add(Box.createVerticalStrut(12));
        JPanel total=new JPanel(new BorderLayout());total.setOpaque(false);total.add(new JLabel("ESTIMATED TOTAL"),BorderLayout.WEST);totalLabel=new JLabel("₹0");totalLabel.setFont(new Font("Segoe UI",Font.BOLD,22));totalLabel.setForeground(ACCENT);total.add(totalLabel,BorderLayout.EAST);form.add(total);form.add(Box.createVerticalStrut(10));JButton review=button("REVIEW & PAY",true);form.add(review);p.add(form,BorderLayout.CENTER);
        check.addActionListener(e->checkAvailability());review.addActionListener(e->{if(validateBooking()){showPayment();}});return p;
    }

    private boolean isWithinTwoHours(String selectedTime) {
        try {
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
            java.time.LocalTime requested = java.time.LocalTime.parse(selectedTime, fmt);
            java.time.LocalTime now = java.time.LocalTime.now();
            long minutes = java.time.Duration.between(now, requested).toMinutes();
            return minutes >= 0 && minutes <= 120;
        } catch (Exception ex) {
            return false;
        }
    }

    private void showTiffin(){
        baseFrame();
        JPanel r = root();

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(34, 55, 34, 55));

        JLabel h = new JLabel("Tiffin & Doorstep Dining");
        h.setFont(SERIF);
        h.setForeground(INK);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(h);
        card.add(Box.createVerticalStrut(8));

        JLabel instructions = hint("Choose your cuisine, food preference, number of tiffins and delivery service. Restaurant-style meals delivered to your doorstep.");
        instructions.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(instructions);
        card.add(Box.createVerticalStrut(18));

        JTextField fullName = field("");
        JTextField phone = field("");
        JTextField tiffinDate = field("");
        tiffinDate.setText(LocalDate.now().format(DATE_FMT));
        JComboBox<String> cuisine = combo(
                "Gujarati", "Punjabi", "South Indian", "Rajasthani", "Bengali",
                "Maharashtrian", "Mughlai", "Italian", "Chinese", "Mexican", "Thai", "Japanese"
        );
        JComboBox<Chef> tiffinChef = new JComboBox<>();
        tiffinChef.setFont(BODY);
        tiffinChef.setPreferredSize(new Dimension(10, 40));
        JComboBox<String> preference = combo("Vegetarian", "Non-Vegetarian", "Eggitarian");
        JComboBox<String> people = combo("1", "2", "3", "4", "5", "6", "7", "8");
        JComboBox<String> service = combo(
                "Self pickup / self delivery",
                "Doorstep delivery",
                "Doorstep delivery + serving & restaurant-style plating"
        );
        JTextField address = field("");
        JTextField request = field("");
        JComboBox<String> deliveryTime = combo(
                "07:00 AM", "07:30 AM", "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM",
                "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
                "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM",
                "04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM", "06:00 PM", "06:30 PM",
                "07:00 PM", "07:30 PM", "08:00 PM", "08:30 PM", "09:00 PM", "09:30 PM", "10:00 PM"
        );

        if (currentUser != null) {
            fullName.setText(currentUser.getName());
            phone.setText(currentUser.getPhone());
        }
        card.add(formBlock("FULL NAME *", fullName, "Required — enter your first and last name."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("PHONE NUMBER *", phone, "Required — enter exactly 10 digits."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("DATE *", tiffinDate, "Required — format: DD-MM-YYYY. Date cannot be in the past."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("CUISINE", cuisine, "Choose the cuisine for your tiffin meal."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("CHOOSE YOUR CHEF", tiffinChef, "Find a chef who can prepare your selected cuisine for tiffin and delivery."));
        card.add(Box.createVerticalStrut(10));
        JPanel tiffinChefCard = new JPanel(new BorderLayout(14, 0));
        tiffinChefCard.setBackground(new Color(251,247,242));
        tiffinChefCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.add(tiffinChefCard);
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("FOOD PREFERENCE", preference, "Vegetarian, non-vegetarian or eggitarian."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("NUMBER OF TIFFINS", people, "Choose how many fresh meal boxes you need."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("DELIVERY / SERVING", service, "Choose pickup, doorstep delivery or delivery with serving and plating."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("DELIVERY TIME", deliveryTime, "Choose the time at which you want your tiffin delivered."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("DELIVERY ADDRESS", address, "Required for doorstep delivery or serving."));
        card.add(Box.createVerticalStrut(10));
        card.add(formBlock("SPECIAL REQUESTS (OPTIONAL)", request, "Add spice preferences, dishes or other meal requests."));
        card.add(Box.createVerticalStrut(16));

        JPanel urgentPanel = new JPanel(new BorderLayout(8, 0));
        urgentPanel.setBackground(LIGHT);
        urgentPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        urgentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JCheckBox withinTwoHours = new JCheckBox("I need my tiffin within 2 hours");
        withinTwoHours.setOpaque(false);
        withinTwoHours.setFont(BOLD);
        withinTwoHours.setForeground(INK);
        JLabel urgentFeeLabel = new JLabel("+₹100 urgent fee");
        urgentFeeLabel.setForeground(ACCENT);
        urgentFeeLabel.setFont(SMALL);
        urgentPanel.add(withinTwoHours, BorderLayout.CENTER);
        urgentPanel.add(urgentFeeLabel, BorderLayout.EAST);
        card.add(urgentPanel);
        card.add(Box.createVerticalStrut(6));

        JLabel urgentNote = hint("If you select within 2 hours, an additional urgent-order fee will be added.");
        urgentNote.setForeground(ACCENT);
        urgentNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(urgentNote);
        card.add(Box.createVerticalStrut(6));

        JLabel cost = new JLabel("Estimated total: ₹200 per tiffin + service fee");
        cost.setFont(BOLD);
        cost.setForeground(ACCENT);
        cost.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(cost);
        card.add(Box.createVerticalStrut(16));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton order = button("ORDER TIFFIN", true);
        JButton back = button("BACK", false);
        row.add(order);
        row.add(back);
        card.add(row);

        Runnable updateTiffinChef = () -> {
            String selectedCuisine = (String)cuisine.getSelectedItem();
            tiffinChef.removeAllItems();
            java.util.List<Chef> matches = ChefData.CHEFS.get(selectedCuisine);
            if (matches != null) {
                for (Chef chef : matches) tiffinChef.addItem(chef);
            }
            if (tiffinChef.getItemCount() > 0) tiffinChef.setSelectedIndex(0);
            tiffinChefCard.removeAll();
            Chef chef = (Chef)tiffinChef.getSelectedItem();
            if (chef != null) {
                JPanel info = new JPanel();
                info.setOpaque(false);
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                JLabel name = new JLabel(chef.getName());
                name.setFont(new Font("Segoe UI", Font.BOLD, 18));
                name.setForeground(INK);
                info.add(name);
                JLabel meta = new JLabel("★ " + chef.getRating() + "   •   " + chef.getExperience() + "   •   ₹" + chef.getPrice() + "/person");
                meta.setForeground(MUTED);
                info.add(meta);
                JLabel specialty = new JLabel("<html><b>Speciality:</b> " + chef.getSpeciality() + "</html>");
                specialty.setFont(SMALL);
                info.add(specialty);
                JLabel dish = new JLabel("<html><b>Dish experience:</b> " + chef.getDishExperience() + "</html>");
                dish.setFont(SMALL);
                info.add(dish);
                JLabel availability = new JLabel("Usually available: " + chef.getAvailability());
                availability.setFont(SMALL);
                availability.setForeground(GREEN);
                info.add(availability);
                tiffinChefCard.add(info, BorderLayout.CENTER);
            }
            tiffinChefCard.revalidate();
            tiffinChefCard.repaint();
        };
        cuisine.addActionListener(e -> updateTiffinChef.run());
        tiffinChef.addActionListener(e -> updateTiffinChef.run());
        updateTiffinChef.run();

        Runnable updateTiffinPrice = () -> {
            int count = Integer.parseInt((String)people.getSelectedItem());
            String selectedService = (String)service.getSelectedItem();
            int basePrice = 200 * count;
            int serviceFee = selectedService.contains("serving") ? 150 : (selectedService.startsWith("Self") ? 0 : 60);
            boolean urgent = withinTwoHours.isSelected();
            int urgentFee = urgent ? 100 : 0;
            int total = basePrice + serviceFee + urgentFee;
            cost.setText(urgent
                    ? "Estimated total: ₹" + total + "  (includes ₹100 urgent-order fee)"
                    : "Estimated total: ₹" + total + " per selected tiffin/service");
        };
        people.addActionListener(e -> updateTiffinPrice.run());
        service.addActionListener(e -> updateTiffinPrice.run());
        deliveryTime.addActionListener(e -> updateTiffinPrice.run());
        withinTwoHours.addActionListener(e -> updateTiffinPrice.run());
        updateTiffinPrice.run();

        order.addActionListener(e -> {
            String customerName = fullName.getText().trim();
            String customerPhone = phone.getText().trim();
            String selectedDate = tiffinDate.getText().trim();
            String selectedCuisine = (String)cuisine.getSelectedItem();
            Chef selectedChef = (Chef)tiffinChef.getSelectedItem();
            String selectedPreference = (String)preference.getSelectedItem();
            String selectedPeople = (String)people.getSelectedItem();
            String selectedService = (String)service.getSelectedItem();
            String selectedTime = (String)deliveryTime.getSelectedItem();

            if (!Validation.validName(customerName)) {
                msg("Full name is mandatory. Please enter your first and last name.", true); fullName.requestFocusInWindow(); return;
            }
            if (!Validation.validPhone(customerPhone)) {
                msg("Phone number is mandatory and must contain exactly 10 digits.", true); phone.requestFocusInWindow(); return;
            }
            try {
                LocalDate d = LocalDate.parse(selectedDate, DATE_FMT);
                if (d.isBefore(LocalDate.now())) {
                    msg("Tiffin date cannot be in the past.", true); tiffinDate.requestFocusInWindow(); return;
                }
            } catch (Exception ex) {
                msg("Tiffin date is mandatory. Enter the date in DD-MM-YYYY format.", true); tiffinDate.requestFocusInWindow(); return;
            }
            if (selectedCuisine == null || selectedCuisine.trim().isEmpty()) {
                msg("Please choose a cuisine.", true); cuisine.requestFocusInWindow(); return;
            }
            if (selectedChef == null) {
                msg("Please choose a chef.", true); tiffinChef.requestFocusInWindow(); return;
            }
            if (selectedPreference == null || selectedPreference.trim().isEmpty()) {
                msg("Please choose a food preference.", true); preference.requestFocusInWindow(); return;
            }
            if (selectedPeople == null || selectedPeople.trim().isEmpty()) {
                msg("Please choose the number of tiffins.", true); people.requestFocusInWindow(); return;
            }
            if (selectedService == null || selectedService.trim().isEmpty()) {
                msg("Please choose the delivery / serving option.", true); service.requestFocusInWindow(); return;
            }
            if (selectedTime == null || selectedTime.trim().isEmpty()) {
                msg("Please choose the delivery time.", true); deliveryTime.requestFocusInWindow(); return;
            }
            if (!Validation.validAddress(address.getText())) {
                msg("Delivery address is mandatory. Please enter house/flat number, area, city, state and 6-digit pincode.", true);
                address.requestFocusInWindow();
                return;
            }
            String selectedServiceFinal = selectedService;
            int count = Integer.parseInt(selectedPeople);
            boolean delivery = !selectedServiceFinal.startsWith("Self");
            int base = 200 * count;
            int serviceFee = selectedServiceFinal.contains("serving") ? 150 : (delivery ? 60 : 0);
            boolean urgent = withinTwoHours.isSelected();
            int urgentFee = urgent ? 100 : 0;
            int total = base + serviceFee + urgentFee;
            Chef kitchen = selectedChef;
            showTiffinPayment(kitchen, selectedPreference, selectedPeople, count, selectedDate,
                    selectedTime, customerName, customerPhone, address.getText().trim(),
                    request.getText().trim(), selectedServiceFinal, total, urgent);
        });
        back.addActionListener(e -> showHome());

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        r.add(scroll, BorderLayout.CENTER);
        add(r);
        revalidate();
        repaint();
    }

    private void limitDigits(){ limitDigits(phoneField, 10); }

    private void limitDigits(JTextField field){ limitDigits(field, 10); }

    private void limitDigits(JPasswordField field, int maxDigits){
        String current = new String(field.getPassword());
        String cleaned = current.replaceAll("\\D", "");
        if(cleaned.length() > maxDigits) cleaned = cleaned.substring(0, maxDigits);
        if(!current.equals(cleaned)) field.setText(cleaned);
    }

    private void limitDigits(JTextField field, int maxDigits){
        String current=field.getText();
        String cleaned=current.replaceAll("\\D","");
        if(cleaned.length()>maxDigits) cleaned=cleaned.substring(0,maxDigits);
        final String value=cleaned;
        if(!value.equals(current)) SwingUtilities.invokeLater(()->field.setText(value));
    }

    private void populateChefBox(){
        if(cuisineBox==null || chefBox==null)return;
        String cuisine=(String)cuisineBox.getSelectedItem();
        chefBox.removeAllItems();
        if(cuisine!=null){
            for(Chef chef: ChefData.CHEFS.get(cuisine)) chefBox.addItem(chef);
        }
        if(chefBox.getItemCount()>0) chefBox.setSelectedIndex(0);
        updateChef();
    }

    private void updateChef(){
        if(cuisineBox==null || chefBox==null || chefBox.getSelectedItem()==null)return;
        selectedChef=(Chef)chefBox.getSelectedItem();
        // During the first construction of the home screen, the chef combo box
        // fires before the booking panel has created these components.
        // Do not touch them until the complete screen exists.
        if(totalLabel==null || chefCard==null || guestsBox==null)return;
        int guests=Integer.parseInt((String)guestsBox.getSelectedItem());
        int base=selectedChef.getPrice()*guests;
        int total=immediate?(int)Math.round(base*1.30):base;
        totalLabel.setText("₹"+total);
        chefCard.removeAll();

        JLabel img=new JLabel(placeholderChefIcon(130,130));
        img.setPreferredSize(new Dimension(130,130));
        chefCard.add(img,BorderLayout.WEST);
        loadChefImageAsync(selectedChef.getImageUrl(),130,130,img);

        JPanel info=new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
        chefName=new JLabel(selectedChef.getName());
        chefName.setFont(new Font("Segoe UI",Font.BOLD,19));
        chefName.setForeground(INK);
        info.add(chefName);
        chefMeta=new JLabel("★ "+selectedChef.getRating()+"   •   "+selectedChef.getExperience()+"   •   ₹"+selectedChef.getPrice()+"/person");
        chefMeta.setForeground(MUTED);
        info.add(chefMeta);
        chefSpeciality=new JLabel("<html><b>Speciality:</b> "+selectedChef.getSpeciality()+"</html>");
        chefSpeciality.setFont(SMALL);
        info.add(chefSpeciality);
        chefDish=new JLabel("<html><b>Dish experience:</b> "+selectedChef.getDishExperience()+"</html>");
        chefDish.setFont(SMALL);
        info.add(chefDish);
        JLabel av=new JLabel("Usually available: "+selectedChef.getAvailability());
        av.setFont(SMALL);
        av.setForeground(GREEN);
        info.add(av);
        JLabel phone=new JLabel("Chef contact: "+selectedChef.getPhone());
        phone.setFont(SMALL);
        phone.setForeground(ACCENT);
        info.add(phone);
        chefPrice=new JLabel(immediate?"Priority booking: +30% for within 2 hours":"Standard booking rate");
        chefPrice.setForeground(immediate?ACCENT:GREEN);
        chefPrice.setFont(BOLD);
        info.add(chefPrice);
        chefCard.add(info,BorderLayout.CENTER);
        chefCard.revalidate();
        chefCard.repaint();
    }

    private ImageIcon loadChefImage(String url,int w,int h){
        ImageIcon cached=imageCache.get(url);
        if(cached!=null)return cached;
        return placeholderChefIcon(w,h);
    }

    private void loadChefImageAsync(String url,int w,int h,JLabel target){
        ImageIcon cached=imageCache.get(url);
        if(cached!=null){target.setIcon(cached);return;}
        SwingWorker<ImageIcon,Void> worker=new SwingWorker<ImageIcon,Void>(){
            protected ImageIcon doInBackground(){
                try{
                    BufferedImage im=ImageIO.read(new URL(url));
                    if(im==null)return null;
                    Image scaled=im.getScaledInstance(w,h,Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }catch(Exception ex){return null;}
            }
            protected void done(){
                try{
                    ImageIcon icon=get();
                    if(icon!=null){
                        imageCache.put(url,icon);
                        target.setIcon(icon);
                        target.revalidate();
                        target.repaint();
                    }
                }catch(Exception ignored){}
            }
        };
        worker.execute();
    }

    private ImageIcon placeholderChefIcon(int w,int h){
        BufferedImage b=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=b.createGraphics();
        g.setColor(new Color(78,59,49));
        g.fillRect(0,0,w,h);
        g.setColor(new Color(244,224,205));
        g.fillOval(w/2-30,20,60,60);
        g.setColor(Color.WHITE);
        g.fillRoundRect(22,78,w-44,45,18,18);
        g.dispose();
        return new ImageIcon(b);
    }

    private void checkAvailability(){try{LocalDate d=LocalDate.parse(dateField.getText().trim(),DATE_FMT);if(d.isBefore(LocalDate.now())){availabilityLabel.setText("✕ Date is in the past");availabilityLabel.setForeground(RED);availabilityChecked=false;return;}String t=(String)timeBox.getSelectedItem();int hour=parseHour(t);if(hour<8||hour>21){availabilityLabel.setText("✕ Chef unavailable at this time");availabilityLabel.setForeground(RED);availabilityChecked=false;return;}availabilityChecked=true;availabilityLabel.setText("✓ Slot looks available • "+selectedChef.getAvailability());availabilityLabel.setForeground(GREEN);}catch(Exception e){availabilityLabel.setText("✕ Use date format DD-MM-YYYY");availabilityLabel.setForeground(RED);availabilityChecked=false;}}
    private int parseHour(String s){String[] p=s.split(":| ");int h=Integer.parseInt(p[0]);boolean pm=s.endsWith("PM");if(pm&&h<12)h+=12;if(!pm&&h==12)h=0;return h;}

    private boolean validateBooking(){
        if(cuisineBox==null || cuisineBox.getSelectedItem()==null || cuisineBox.getSelectedItem().toString().trim().isEmpty()){msg("Please choose a cuisine.",true);return false;}
        if(chefBox==null || chefBox.getSelectedItem()==null){msg("Please choose a chef.",true);return false;}
        if(preferenceBox==null || preferenceBox.getSelectedItem()==null || preferenceBox.getSelectedItem().toString().trim().isEmpty()){msg("Please choose a food preference.",true);return false;}
        if(guestsBox==null || guestsBox.getSelectedItem()==null || guestsBox.getSelectedItem().toString().trim().isEmpty()){msg("Please choose the number of guests.",true);return false;}
        if(dateField.getText().trim().isEmpty()){msg("Appointment date is mandatory.",true);dateField.requestFocusInWindow();return false;}
        try{LocalDate d=LocalDate.parse(dateField.getText().trim(),DATE_FMT);if(d.isBefore(LocalDate.now())){msg("Appointment date cannot be in the past.",true);dateField.requestFocusInWindow();return false;}}catch(Exception e){msg("Enter the appointment date in DD-MM-YYYY format.",true);dateField.requestFocusInWindow();return false;}
        if(timeBox==null || timeBox.getSelectedItem()==null || timeBox.getSelectedItem().toString().trim().isEmpty()){msg("Please choose an appointment time.",true);return false;}
        if(!Validation.validName(nameField.getText())){msg("Please enter your full name (first and last name).",true);nameField.requestFocusInWindow();return false;}
        if(!Validation.validPhone(phoneField.getText())){msg("Phone number must contain exactly 10 digits.",true);phoneField.requestFocusInWindow();return false;}
        if(!Validation.validAddress(addressField.getText())){msg("Address must include house/flat number, area, city, state and a valid 6-digit pincode. Example: 24, MG Road, Alkapuri, Vadodara, Gujarat, 390007",true);addressField.requestFocusInWindow();return false;}
        if(!availabilityChecked){msg("Please click CHECK CHEF AVAILABILITY before continuing.",true);return false;}
        if(immediate){try{LocalDate d=LocalDate.parse(dateField.getText().trim(),DATE_FMT);if(!d.equals(LocalDate.now())){msg("For an immediate booking, select today's date.",true);return false;}}catch(Exception e){return false;}}
        return true;
    }

    private void limitCardDigits(JTextField f){
        SwingUtilities.invokeLater(()->{
            String digits=f.getText().replaceAll("\\D", "");
            if(digits.length()>16) digits=digits.substring(0,16);
            StringBuilder out=new StringBuilder();
            for(int i=0;i<digits.length();i++){
                if(i>0 && i%4==0) out.append(' ');
                out.append(digits.charAt(i));
            }
            if(!f.getText().equals(out.toString())){
                f.setText(out.toString());
                f.setCaretPosition(f.getText().length());
            }
        });
    }

    private void showTiffinPayment(Chef kitchen, String preference, String people, int count, String date,
                                   String deliveryTime, String customerName, String customerPhone,
                                   String address, String request, String service, int total, boolean urgent){
        JDialog d = new JDialog(this, "Secure Payment — ChefOnCall", true);
        d.setSize(720, 560);
        d.setLocationRelativeTo(this);

        JPanel r = new JPanel(new BorderLayout(18,18));
        r.setBackground(BG);
        r.setBorder(new EmptyBorder(22,25,22,25));

        JPanel summary = new JPanel();
        summary.setBackground(new Color(47,36,31));
        summary.setBorder(new EmptyBorder(20,20,20,20));
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        JLabel sh = new JLabel("TIFFIN ORDER SUMMARY");
        sh.setForeground(new Color(225,190,163));
        sh.setFont(BOLD);
        summary.add(sh);
        summary.add(Box.createVerticalStrut(15));
        JLabel sn = new JLabel("<html><font color='white'><b>" + kitchen.getName() +
                "</b><br>" + kitchen.getCuisine() + " • " + people + " tiffin(s)<br>" +
                date + " • " + deliveryTime + "</font></html>");
        sn.setFont(BODY);
        summary.add(sn);
        summary.add(Box.createVerticalGlue());
        JLabel amt = new JLabel("₹" + total);
        amt.setForeground(Color.WHITE);
        amt.setFont(new Font("Segoe UI",Font.BOLD,30));
        summary.add(amt);
        JLabel inc = new JLabel(urgent ? "Includes ₹100 urgent fee" : "Standard tiffin rate");
        inc.setForeground(new Color(225,190,163));
        summary.add(inc);

        JPanel pay = new JPanel();
        pay.setBackground(CARD);
        pay.setBorder(new EmptyBorder(20,25,20,25));
        pay.setLayout(new BoxLayout(pay, BoxLayout.Y_AXIS));
        JLabel ph = new JLabel("Choose payment method");
        ph.setFont(TITLE);
        ph.setForeground(INK);
        pay.add(ph);
        pay.add(Box.createVerticalStrut(15));

        JRadioButton upi = new JRadioButton("UPI", true);
        JRadioButton card = new JRadioButton("Debit / Credit Card");
        JRadioButton cash = new JRadioButton("Pay on delivery / at service");
        for(JRadioButton rb : new JRadioButton[]{upi,card,cash}){
            rb.setOpaque(false);
            rb.setFont(BODY);
        }
        ButtonGroup bg = new ButtonGroup();
        bg.add(upi); bg.add(card); bg.add(cash);
        pay.add(upi); pay.add(card); pay.add(cash);
        pay.add(Box.createVerticalStrut(12));

        JTextField upiField = field("");
        JTextField cardField = field("");
        JTextField exp = field("");
        JTextField cvv = field("");
        cardField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ limitCardDigits(cardField); }
            public void removeUpdate(DocumentEvent e){ limitCardDigits(cardField); }
            public void changedUpdate(DocumentEvent e){}
        });
        cvv.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ limitDigits(cvv,3); }
            public void removeUpdate(DocumentEvent e){ limitDigits(cvv,3); }
            public void changedUpdate(DocumentEvent e){}
        });
        exp.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ formatExpiry(exp); }
            public void removeUpdate(DocumentEvent e){ formatExpiry(exp); }
            public void changedUpdate(DocumentEvent e){}
        });

        JPanel details = new JPanel(new GridLayout(2,1,0,8));
        details.setOpaque(false);
        details.add(formBlock("UPI ID", upiField, "Example: name@upi"));
        pay.add(details);
        upi.addActionListener(e -> {
            details.removeAll();
            details.add(formBlock("UPI ID", upiField, "Example: name@upi"));
            details.revalidate(); details.repaint();
        });
        card.addActionListener(e -> {
            details.removeAll();
            JPanel cp = new JPanel(new GridLayout(1,1)); cp.setOpaque(false);
            cp.add(formBlock("CARD NUMBER", cardField, "16 digits"));
            details.add(cp);
            JPanel row = new JPanel(new GridLayout(1,2,8,0)); row.setOpaque(false);
            row.add(formBlock("EXPIRY", exp, "MM/YY"));
            row.add(formBlock("CVV", cvv, "3 digits"));
            details.add(row);
            details.revalidate(); details.repaint();
        });
        cash.addActionListener(e -> {
            details.removeAll();
            details.add(hint("No online payment. Pay when the tiffin is delivered or service is provided."));
            details.revalidate(); details.repaint();
        });

        JButton payBtn = button("PAY ₹" + total + " & CONFIRM", true);
        pay.add(Box.createVerticalStrut(12));
        pay.add(payBtn);
        pay.add(Box.createVerticalStrut(8));
        pay.add(hint("Frontend demo only — no real money is charged."));

        r.add(summary, BorderLayout.WEST);
        summary.setPreferredSize(new Dimension(250,0));
        r.add(pay, BorderLayout.CENTER);
        d.setContentPane(r);

        payBtn.addActionListener(e -> {
            String method;
            if(upi.isSelected()){
                if(!Validation.validUpi(upiField.getText())){
                    msg("Enter a valid UPI ID like name@upi.", true);
                    return;
                }
                method = "UPI";
            } else if(card.isSelected()){
                if(!Validation.validCard(cardField.getText(), exp.getText(), cvv.getText())){
                    msg("Please enter a valid debit/credit card: 16 digits, expiry in MM/YY format, and 3-digit CVV. The expiry must not be in the past.", true);
                    return;
                }
                method = "Card";
            } else {
                method = "Pay on delivery / at service";
            }

            String ref = "TIF" + (System.currentTimeMillis() % 1000000);
            Booking b = new Booking(ref, kitchen, preference, people, count, date,
                    service + " at " + deliveryTime, customerName, customerPhone, address,
                    request, method, total);

            d.dispose();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Please review your tiffin order.\nReference: " + ref +
                    "\nDelivery time: " + deliveryTime +
                    (urgent ? "\nUrgent order: ₹100 additional fee (within 2 hours)" : "") +
                    "\nPayment: " + method + "\nTotal: ₹" + total +
                    "\n\nDo you want to place this order?",
                    "Confirm Tiffin Order", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if(confirm == JOptionPane.YES_OPTION){
                currentUser.getBookings().add(b);
                showBookings();
            }
        });
        d.setVisible(true);
    }

    private void formatExpiry(JTextField f){
        SwingUtilities.invokeLater(()->{
            String digits=f.getText().replaceAll("\\D", "");
            if(digits.length()>4) digits=digits.substring(0,4);
            String out=digits.length()>2 ? digits.substring(0,2)+"/"+digits.substring(2) : digits;
            if(!f.getText().equals(out)){
                f.setText(out);
                f.setCaretPosition(f.getText().length());
            }
        });
    }

    private void showPayment(){int guests=Integer.parseInt((String)guestsBox.getSelectedItem());int base=selectedChef.getPrice()*guests;int priority=immediate?(int)Math.round(base*.30):0;int total=base+priority;JDialog d=new JDialog(this,"Secure Payment — ChefOnCall",true);d.setSize(720,560);d.setLocationRelativeTo(this);JPanel r=new JPanel(new BorderLayout(18,18));r.setBackground(BG);r.setBorder(new EmptyBorder(22,25,22,25));JPanel summary=new JPanel();summary.setBackground(new Color(47,36,31));summary.setBorder(new EmptyBorder(20,20,20,20));summary.setLayout(new BoxLayout(summary,BoxLayout.Y_AXIS));JLabel sh=new JLabel("ORDER SUMMARY");sh.setForeground(new Color(225,190,163));sh.setFont(BOLD);summary.add(sh);summary.add(Box.createVerticalStrut(15));JLabel sn=new JLabel("<html><font color='white'><b>"+selectedChef.getName()+"</b><br>"+selectedChef.getCuisine()+" • "+guests+" guest(s)<br>"+dateField.getText()+" • "+timeBox.getSelectedItem()+"</font></html>");sn.setFont(BODY);summary.add(sn);summary.add(Box.createVerticalGlue());JLabel amt=new JLabel("₹"+total);amt.setForeground(Color.WHITE);amt.setFont(new Font("Segoe UI",Font.BOLD,30));summary.add(amt);JLabel inc=new JLabel("Includes "+(immediate?"30% priority fee":"standard rate"));inc.setForeground(new Color(225,190,163));summary.add(inc);
        JPanel pay=new JPanel();pay.setBackground(CARD);pay.setBorder(new EmptyBorder(20,25,20,25));pay.setLayout(new BoxLayout(pay,BoxLayout.Y_AXIS));JLabel ph=new JLabel("Choose payment method");ph.setFont(TITLE);ph.setForeground(INK);pay.add(ph);pay.add(Box.createVerticalStrut(15));JRadioButton upi=new JRadioButton("UPI",true),card=new JRadioButton("Debit / Credit Card"),cash=new JRadioButton("Pay at appointment");for(JRadioButton rb:new JRadioButton[]{upi,card,cash}){rb.setOpaque(false);rb.setFont(BODY);}ButtonGroup bg=new ButtonGroup();bg.add(upi);bg.add(card);bg.add(cash);pay.add(upi);pay.add(card);pay.add(cash);pay.add(Box.createVerticalStrut(12));JTextField upiField=field("");upiField.setText("");JTextField cardField=field("");JTextField exp=field("");JTextField cvv=field("");
        cardField.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){limitCardDigits(cardField); } public void removeUpdate(DocumentEvent e){limitCardDigits(cardField); } public void changedUpdate(DocumentEvent e){}});
        cvv.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){limitDigits(cvv,3); } public void removeUpdate(DocumentEvent e){} public void changedUpdate(DocumentEvent e){}});
        exp.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){formatExpiry(exp); } public void removeUpdate(DocumentEvent e){} public void changedUpdate(DocumentEvent e){}});
        JPanel details=new JPanel(new GridLayout(2,1,0,8));details.setOpaque(false);details.add(formBlock("UPI ID",upiField,"Example: name@upi"));pay.add(details);upi.addActionListener(e->{details.removeAll();details.add(formBlock("UPI ID",upiField,"Example: name@upi"));details.revalidate();details.repaint();});card.addActionListener(e->{details.removeAll();JPanel cp=new JPanel(new GridLayout(1,1));cp.setOpaque(false);cp.add(formBlock("CARD NUMBER",cardField,"16 digits"));details.add(cp);JPanel row=new JPanel(new GridLayout(1,2,8,0));row.setOpaque(false);row.add(formBlock("EXPIRY",exp,"MM/YY"));row.add(formBlock("CVV",cvv,"3 digits"));details.add(row);details.revalidate();details.repaint();});cash.addActionListener(e->{details.removeAll();details.add(hint("No online payment. Pay the chef at the appointment. Your booking will still be confirmed."));details.revalidate();details.repaint();});JButton payBtn=button("PAY ₹"+total+" & CONFIRM",true);pay.add(Box.createVerticalStrut(12));pay.add(payBtn);pay.add(Box.createVerticalStrut(8));pay.add(hint("Frontend demo only — no real money is charged."));r.add(summary,BorderLayout.WEST);summary.setPreferredSize(new Dimension(250,0));r.add(pay,BorderLayout.CENTER);d.setContentPane(r);payBtn.addActionListener(e->{String method;if(upi.isSelected()){if(!Validation.validUpi(upiField.getText())){msg("Enter a valid UPI ID like name@upi.",true);return;}method="UPI";}else if(card.isSelected()){if(!Validation.validCard(cardField.getText(),exp.getText(),cvv.getText())){msg("Please enter a valid debit/credit card: 16 digits, expiry in MM/YY format, and 3-digit CVV. The expiry must not be in the past.",true);return;}method="Card";}else method="Pay at appointment";String ref="COC"+System.currentTimeMillis()%1000000;Booking b=new Booking(ref,selectedChef,(String)preferenceBox.getSelectedItem(),(String)guestsBox.getSelectedItem(),guests,dateField.getText(),(String)timeBox.getSelectedItem(),nameField.getText(),phoneField.getText(),addressField.getText(),requestField.getText(),method,total);d.dispose();JOptionPane.showMessageDialog(this,"Please review your appointment.\nReference: "+ref+"\nRefund policy: cancellation before the appointment starts will initiate a refund.\n\nPress OK to place the booking.","Confirm Appointment",JOptionPane.INFORMATION_MESSAGE);currentUser.getBookings().add(b);showBookings();});d.setVisible(true);}

    private void showBookings(){baseFrame();JPanel r=root();JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);JLabel h=new JLabel("My bookings");h.setFont(SERIF);h.setForeground(INK);top.add(h,BorderLayout.WEST);JButton back=button("BACK TO CHEFS",false);top.add(back,BorderLayout.EAST);r.add(top,BorderLayout.NORTH);back.addActionListener(e->showHome());bookingsPanel=new JPanel();bookingsPanel.setBackground(BG);bookingsPanel.setLayout(new BoxLayout(bookingsPanel,BoxLayout.Y_AXIS));if(currentUser.getBookings().isEmpty()){bookingsPanel.add(Box.createVerticalStrut(70));JLabel empty=new JLabel("No bookings yet. Your confirmed appointments will appear here.");empty.setFont(TITLE);empty.setForeground(MUTED);bookingsPanel.add(empty);}else{for(Booking b:currentUser.getBookings())addBookingCard(b);}JScrollPane sp=new JScrollPane(bookingsPanel);sp.setBorder(null);sp.getVerticalScrollBar().setUnitIncrement(14);r.add(sp,BorderLayout.CENTER);add(r);revalidate();repaint();}

    private void addBookingCard(Booking b){JPanel c=new JPanel(new BorderLayout(18,0));c.setBackground(CARD);c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(224,214,204)),new EmptyBorder(16,18,16,18)));c.setMaximumSize(new Dimension(Integer.MAX_VALUE,170));JLabel photo=new JLabel(placeholderChefIcon(95,95));c.add(photo,BorderLayout.WEST);loadChefImageAsync(b.getChef().getImageUrl(),95,95,photo);JPanel info=new JPanel();info.setOpaque(false);info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));JLabel title=new JLabel(b.getChef().getName()+"  •  "+b.getChef().getCuisine());title.setFont(TITLE);title.setForeground(INK);info.add(title);info.add(new JLabel(b.getDate()+" • "+b.getTime()+" • "+b.getGuestCount()+" guest(s)"));info.add(new JLabel("₹"+b.getTotal()+" • "+b.getPaymentMethod()+" • Ref: "+b.getReference()));JLabel status=new JLabel(b.getStatus());status.setFont(BOLD);status.setForeground(b.getStatus().startsWith("Cancelled")?RED:GREEN);info.add(status);JButton cancel=button("CANCEL & REFUND",false);cancel.setEnabled(!b.getStatus().startsWith("Cancelled"));cancel.addActionListener(e->{int ans=JOptionPane.showConfirmDialog(this,"Cancel this booking?\nA refund will be initiated to your payment method.\nTypical demo refund timeline: 3–5 business days.","Cancel booking",JOptionPane.YES_NO_OPTION);if(ans==JOptionPane.YES_OPTION){b.cancel();showBookings();}});JPanel right=new JPanel(new BorderLayout());right.setOpaque(false);right.add(new JLabel("₹"+b.getTotal()),BorderLayout.NORTH);right.add(cancel,BorderLayout.SOUTH);c.add(info,BorderLayout.CENTER);c.add(right,BorderLayout.EAST);bookingsPanel.add(c);bookingsPanel.add(Box.createVerticalStrut(12));}

    private class DishCarouselPanel extends JPanel {
        private final String[] names = {"Paneer Tikka", "Masala Dosa", "Butter Chicken"};
        private final String[] urls = {
            "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto,w_600,h_600,c_fit/FOOD_CATALOG/IMAGES/CMS/2025/7/29/dcf726ba-6e7b-42bf-95c3-6f1e103db9da_a94a8054-7c2a-4cbb-902e-dd37ffb92d61.png",
            "https://media-assets.swiggy.com/swiggy/image/upload/fl_lossy,f_auto,q_auto/FOOD_CATALOG/IMAGES/CMS/2026/3/3/972a6ac1-2898-4dfb-be66-5e3cfcba903f_8dc8f38b-40ae-4f4b-b55c-24b17d7ce81b.jpg",
            "https://b.zmtcdn.com/data/pictures/3/312063/c66819b757aec01d1c3b49eb4d519aee.jpg"
        };
        private final Image[] images = new Image[3];
        private int index = 0;
        private javax.swing.Timer timer;
        private SwingWorker<Image,Void> worker;

        DishCarouselPanel(){
            setOpaque(false);
            setDoubleBuffered(true);
            timer = new javax.swing.Timer(4000, e -> {
                index = (index + 1) % names.length;
                loadCurrentImage();
                repaint();
            });
            // Preload every dish so each image is ready when the carousel reaches it.
            for(int i=0;i<urls.length;i++) loadImage(i);
            timer.start();
        }

        void stop(){
            if(timer!=null) timer.stop();
            if(worker!=null) worker.cancel(true);
        }

        private void loadCurrentImage(){
            if(images[index]!=null){ repaint(); return; }
            loadImage(index);
        }

        private void loadImage(final int requested){
            if(requested<0 || requested>=urls.length || images[requested]!=null) return;
            new SwingWorker<Image,Void>(){
                protected Image doInBackground(){
                    try{
                        URL u = new URL(urls[requested]);
                        java.net.URLConnection c = u.openConnection();
                        c.setConnectTimeout(2500);
                        c.setReadTimeout(3500);
                        c.setUseCaches(true);
                        try(java.io.InputStream in = c.getInputStream()){
                            return ImageIO.read(in);
                        }
                    }catch(Exception ex){ return null; }
                }
                protected void done(){
                    try{
                        Image im = get();
                        if(im!=null) images[requested]=im;
                    }catch(Exception ignored){}
                    repaint();
                }
            }.execute();
        }

        protected void paintComponent(Graphics gr){
            super.paintComponent(gr);
            Graphics2D g=(Graphics2D)gr.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int boxW=Math.min(getWidth()-20,390), boxH=Math.min(getHeight()-20,300);
            int x=(getWidth()-boxW)/2, y=Math.max(12,(getHeight()-boxH)/2);
            g.setColor(new Color(255,255,255,24));
            g.fillRoundRect(x-5,y-5,boxW+10,boxH+10,28,28);
            Shape clip=new java.awt.geom.RoundRectangle2D.Double(x,y,boxW,boxH,24,24);
            g.clip(clip);
            Image im=images[index];
            if(im!=null){
                double scale=Math.max((double)boxW/im.getWidth(null),(double)boxH/im.getHeight(null));
                int w=(int)(im.getWidth(null)*scale), h=(int)(im.getHeight(null)*scale);
                g.drawImage(im,x+(boxW-w)/2,y+(boxH-h)/2,w,h,null);
            }else{
                drawFallbackDish(g,x,y,boxW,boxH,index);
            }
            g.setClip(null);
            g.setColor(new Color(0,0,0,110));
            g.fillRect(x,y+boxH-58,boxW,58);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Georgia",Font.BOLD,21));
            g.drawString(names[index],x+18,y+boxH-31);
            g.setFont(new Font("Segoe UI",Font.PLAIN,12));
            g.drawString("Freshly served • ChefOnCall",x+18,y+boxH-13);
            g.dispose();
        }

        private void drawFallbackDish(Graphics2D g,int x,int y,int w,int h,int i){
            g.setColor(new Color(239,218,196)); g.fillRect(x,y,w,h);
            int cx=x+w/2, cy=y+h/2-8;
            g.setColor(Color.WHITE); g.fillOval(cx-105,cy-70,210,140);
            if(i==0){
                g.setColor(new Color(190,110,45)); g.fillOval(cx-72,cy-38,144,76);
                g.setColor(new Color(236,190,87)); for(int k=0;k<12;k++) g.fillOval(cx-58+(k%4)*32,cy-28+(k/4)*25,12,8);
            }else if(i==1){
                g.setColor(new Color(188,92,54)); for(int k=0;k<5;k++) g.fillRoundRect(cx-65+k*30,cy-25,22,50,12,12);
                g.setColor(new Color(74,126,62)); for(int k=0;k<5;k++) g.fillOval(cx-60+k*30,cy-34,10,10);
            }else if(i==2){
                g.setColor(new Color(218,177,93)); g.fillArc(cx-90,cy-48,180,96,15,150); g.fillArc(cx-90,cy-48,180,96,195,150);
                g.setColor(new Color(120,161,83)); g.fillOval(cx-45,cy-10,90,35);
            }else{
                g.setColor(new Color(161,83,48)); g.fillOval(cx-75,cy-35,150,70);
                g.setColor(new Color(241,205,142)); g.fillOval(cx-35,cy-18,70,36);
            }
        }
    }

    private void msg(String s,boolean error){JOptionPane.showMessageDialog(this,s,"ChefOnCall",error?JOptionPane.ERROR_MESSAGE:JOptionPane.INFORMATION_MESSAGE);}
}
