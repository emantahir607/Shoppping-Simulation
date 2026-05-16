import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * DSA Shopping Simulation — All-in-one file
 *
 * Demonstrates: Graph + Dijkstra, BST, Stack (Cart), Queue (Library), Checkout
 * UI: Full-screen, vertical button panel on the left side
 */
public class DSAProject {

    // ── Shared cart (Stack-based) used by Cart, IceCream & Checkout ──
    static ShoppingCart sharedCart = new ShoppingCart();

    // =========================================================
    //  MAIN
    // =========================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DSA Shopping Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // ── Full-screen setup ──────────────────────────────
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(false); // keep title bar; set true for pure borderless fullscreen

            // ── Main layout: left sidebar + right content area ──
            frame.setLayout(new BorderLayout());

            // ── Title banner ──────────────────────────────────
            JLabel title = new JLabel("  DSA Shopping Simulation", SwingConstants.LEFT);
            title.setFont(new Font("SansSerif", Font.BOLD, 22));
            title.setForeground(Color.WHITE);
            title.setBackground(new Color(30, 60, 120));
            title.setOpaque(true);
            title.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
            frame.add(title, BorderLayout.NORTH);

            // ── Left sidebar with vertical buttons ────────────
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(22, 45, 90));
            sidebar.setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));
            sidebar.setPreferredSize(new Dimension(260, 0));

            String[] labels = {
                "1.  Show Graph",
                "2.  Shortest Path\n    House → Stationary",
                "3.  Show BST",
                "4.  Shopping Cart",
                "5.  Checkout",
                "6.  Library Queue",
                "7.  Issue Book",
                "8.  Ice Cream Shop",
                "9.  Shortest Path\n    IceCream → F1",
                "10. Shortest Path\n    F1 → F2"
            };

            JButton[] buttons = new JButton[labels.length];

            for (int i = 0; i < labels.length; i++) {
                String html = "<html><center>" + labels[i].replace("\n", "<br>") + "</center></html>";
                buttons[i] = makeNavButton(html, i == 0);
                final int idx = i;
                buttons[i].addActionListener(e -> {
                    handleAction(idx);
                    buttons[idx].setEnabled(false);
                    if (idx + 1 < buttons.length) buttons[idx + 1].setEnabled(true);
                });
                sidebar.add(buttons[i]);
                sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
            }

            // Spacer pushes restart button to bottom
            sidebar.add(Box.createVerticalGlue());

            // Restart button
            JButton restart = new JButton("↺  Restart Session");
            styleRestartButton(restart);
            restart.addActionListener(e -> {
                sharedCart.clear();
                for (int i = 0; i < buttons.length; i++)
                    buttons[i].setEnabled(i == 0);
                JOptionPane.showMessageDialog(frame,
                    "Session reset. Cart cleared.", "Reset", JOptionPane.INFORMATION_MESSAGE);
            });
            sidebar.add(restart);

            // ── Right content placeholder ─────────────────────
            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(new Color(245, 247, 252));

            JLabel hint = new JLabel(
                "<html><center><b style='font-size:16px'>Welcome to DSA Shopping Simulation</b><br><br>" +
                "Use the buttons on the left to walk through each step in order.<br>" +
                "Each button unlocks the next one after it is clicked.</center></html>",
                SwingConstants.CENTER);
            hint.setFont(new Font("SansSerif", Font.PLAIN, 14));
            hint.setForeground(new Color(60, 80, 130));
            content.add(hint, BorderLayout.CENTER);

            frame.add(sidebar,  BorderLayout.WEST);
            frame.add(content,  BorderLayout.CENTER);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // ── Helper: create a styled sidebar nav button ──────────
    private static JButton makeNavButton(String html, boolean enabled) {
        JButton btn = new JButton(html);
        btn.setEnabled(enabled);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(50, 90, 170));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(70, 120, 210));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(btn.isEnabled()
                    ? new Color(50, 90, 170)
                    : new Color(35, 55, 100));
            }
        });

        return btn;
    }

    private static void styleRestartButton(JButton btn) {
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(190, 50, 40));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private static void handleAction(int idx) {
        switch (idx) {
            case 0 -> showGraph(false, null);
            case 1 -> showShortestPath(HOUSE, STATIONARY);
            case 2 -> showBST();
            case 3 -> showCart();
            case 4 -> showCheckout();
            case 5 -> showLibraryQueue();
            case 6 -> showLibraryIssue();
            case 7 -> showIceCream();
            case 8 -> showShortestPath(ICE_CREAM, F1);
            case 9 -> showShortestPath(F1, F2);
        }
    }

    // =========================================================
    //  GRAPH  —  nodes & edges
    // =========================================================
    static final int HOUSE = 0, F1 = 1, F2 = 2, STATIONARY = 3, ICE_CREAM = 4, LIBRARY = 5;

    static final String[] NODE_NAMES = {
        "House", "F1: House", "F2: House",
        "Stationary", "Ice-Cream", "Library"
    };

    static final int[][] EDGES = {
        {HOUSE,      ICE_CREAM,  3},
        {ICE_CREAM,  HOUSE,      6},
        {ICE_CREAM,  STATIONARY, 3},
        {STATIONARY, LIBRARY,    2},
        {HOUSE,      F1,         4},
        {F1,         HOUSE,      3},
        {F1,         F2,         2},
        {F2,         HOUSE,      7},
        {F2,         STATIONARY, 11},
        {LIBRARY,    ICE_CREAM,  1},
    };

    private static void showGraph(boolean highlight, Set<String> hlEdges) {
        JFrame frame = new JFrame("Graph Visualisation");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.WHITE);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paintGraph(g2, getWidth(), getHeight(), highlight,
                           hlEdges == null ? Collections.emptySet() : hlEdges,
                           Collections.emptyList());
            }
        };

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void showShortestPath(int src, int dst) {
        int n = NODE_NAMES.length;
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : EDGES) adj.get(e[0]).add(new int[]{e[1], e[2]});

        int[] dist = new int[n], prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE); Arrays.fill(prev, -1);
        dist[src] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{src, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll(); int u = cur[0], d = cur[1];
            if (d > dist[u]) continue;
            for (int[] nb : adj.get(u)) {
                int v = nb[0], w = nb[1];
                if (dist[u] + w < dist[v]) { dist[v] = dist[u] + w; prev[v] = u; pq.offer(new int[]{v, dist[v]}); }
            }
        }

        if (dist[dst] == Integer.MAX_VALUE) {
            JOptionPane.showMessageDialog(null, "No path from " + NODE_NAMES[src] + " to " + NODE_NAMES[dst]);
            return;
        }

        List<Integer> path = new ArrayList<>();
        for (int at = dst; at != -1; at = prev[at]) path.add(0, at);
        Set<String> hlEdges = new HashSet<>();
        for (int i = 0; i < path.size() - 1; i++) hlEdges.add(path.get(i) + "-" + path.get(i + 1));

        StringBuilder pathStr = new StringBuilder();
        for (int i = 0; i < path.size(); i++) { if (i > 0) pathStr.append(" → "); pathStr.append(NODE_NAMES[path.get(i)]); }

        JFrame frame = new JFrame("Shortest Path: " + NODE_NAMES[src] + " → " + NODE_NAMES[dst]);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final List<Integer> finalPath = path;
        final Set<String>   finalHL   = hlEdges;

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.WHITE);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paintGraph(g2, getWidth(), getHeight(), true, finalHL, finalPath);
            }
        };

        JLabel info = new JLabel("  Route: " + pathStr + "   |   Total cost: " + dist[dst], SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.BOLD, 16));
        info.setForeground(new Color(0, 120, 50));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.add(panel, BorderLayout.CENTER);
        frame.add(info,  BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void paintGraph(Graphics2D g2, int W, int H,
                                   boolean highlight, Set<String> hlEdges, List<Integer> pathNodes) {
        int cx = W / 2, cy = H / 2 - 20, r = Math.min(W, H) / 3;
        Point[] pos = new Point[NODE_NAMES.length];
        for (int i = 0; i < NODE_NAMES.length; i++) {
            double angle = 2 * Math.PI * i / NODE_NAMES.length - Math.PI / 2;
            pos[i] = new Point((int)(cx + r * Math.cos(angle)), (int)(cy + r * Math.sin(angle)));
        }

        for (int[] edge : EDGES) {
            int from = edge[0], to = edge[1], cost = edge[2];
            boolean hl = highlight && hlEdges.contains(from + "-" + to);

            double dx = pos[to].x - pos[from].x, dy = pos[to].y - pos[from].y;
            double dist2 = Math.sqrt(dx * dx + dy * dy);
            int ox = (int)(-dy * 12 / dist2), oy = (int)(dx * 12 / dist2);

            int x1 = pos[from].x + ox, y1 = pos[from].y + oy;
            int x2 = pos[to].x   + ox, y2 = pos[to].y   + oy;

            g2.setColor(hl ? new Color(0, 180, 80) : new Color(140, 140, 140));
            g2.setStroke(new BasicStroke(hl ? 3f : 1.5f));
            g2.drawLine(x1, y1, x2, y2);

            double angle = Math.atan2(y2 - y1, x2 - x1);
            int sz = 12;
            g2.drawLine(x2, y2, (int)(x2 - sz * Math.cos(angle - Math.PI / 6)), (int)(y2 - sz * Math.sin(angle - Math.PI / 6)));
            g2.drawLine(x2, y2, (int)(x2 - sz * Math.cos(angle + Math.PI / 6)), (int)(y2 - sz * Math.sin(angle + Math.PI / 6)));

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString(String.valueOf(cost), (x1 + x2) / 2 + ox / 4, (y1 + y2) / 2 + oy / 4);
        }

        int nodeR = 36;
        for (int i = 0; i < NODE_NAMES.length; i++) {
            boolean onPath = pathNodes.contains(i);
            Color fill   = onPath ? new Color(255, 210, 100) : new Color(255, 182, 193);
            Color border = onPath ? new Color(180, 110, 0)   : new Color(180, 80, 100);

            g2.setColor(fill); g2.fillOval(pos[i].x - nodeR, pos[i].y - nodeR, nodeR*2, nodeR*2);
            g2.setColor(border); g2.setStroke(new BasicStroke(onPath ? 3f : 2f));
            g2.drawOval(pos[i].x - nodeR, pos[i].y - nodeR, nodeR*2, nodeR*2);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String[] parts = NODE_NAMES[i].split(" ", 2);
            if (parts.length == 2) {
                g2.drawString(parts[0], pos[i].x - fm.stringWidth(parts[0]) / 2, pos[i].y - 3);
                g2.drawString(parts[1], pos[i].x - fm.stringWidth(parts[1]) / 2, pos[i].y + 13);
            } else {
                g2.drawString(parts[0], pos[i].x - fm.stringWidth(parts[0]) / 2, pos[i].y + 6);
            }
        }

        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int lx = 16, ly = H - 80;
        g2.setColor(new Color(255, 210, 100)); g2.fillOval(lx, ly, 16, 16);
        g2.setColor(Color.BLACK);              g2.drawString("On shortest path", lx + 22, ly + 13);
        g2.setColor(new Color(255, 182, 193)); g2.fillOval(lx, ly + 26, 16, 16);
        g2.setColor(Color.BLACK);              g2.drawString("Other node", lx + 22, ly + 39);
        g2.setColor(new Color(0, 180, 80)); g2.setStroke(new BasicStroke(3f));
        g2.drawLine(lx, ly + 60, lx + 34, ly + 60);
        g2.setColor(Color.BLACK);              g2.drawString("Shortest path edge", lx + 40, ly + 64);
    }

    // =========================================================
    //  BST
    // =========================================================
    static class BSTNode { String name; int price; BSTNode left, right; BSTNode(String n, int p){name=n;price=p;} }
    static BSTNode bstRoot = null;
    static List<String> bstLog = new ArrayList<>();

    private static BSTNode bstInsert(BSTNode node, String name, int price, String dir) {
        if (node == null) { bstLog.add("Insert " + name + " (price=" + price + ")  →  " + dir); return new BSTNode(name, price); }
        if (price <= node.price) node.left  = bstInsert(node.left,  name, price, "left of "  + node.name);
        else                     node.right = bstInsert(node.right, name, price, "right of " + node.name);
        return node;
    }

    private static void showBST() {
        bstRoot = null; bstLog.clear();
        Object[][] items = {{"Pencil",1},{"Pen",6},{"Book",6},{"Eraser",8},{"Sharpener",5},{"Copy",9},{"Register",1}};
        for (Object[] it : items) bstRoot = bstInsert(bstRoot, (String)it[0], (int)it[1], "root");

        JFrame frame = new JFrame("BST — Stationary items ordered by price");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel treePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.WHITE);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("Left child price ≤ parent,   Right child price > parent", 24, 30);
                drawBSTNode(g2, bstRoot, getWidth() / 2, 80, getWidth() / 4);
            }
        };

        JTextArea logArea = new JTextArea(6, 80);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setText("Insertion log (price is the BST key):\n");
        for (String s : bstLog) logArea.append("  " + s + "\n");

        frame.add(treePanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(logArea), BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void drawBSTNode(Graphics2D g, BSTNode node, int x, int y, int xOff) {
        if (node == null) return;
        if (node.left  != null) { g.setColor(new Color(160,160,160)); g.setStroke(new BasicStroke(1.8f)); g.drawLine(x, y, x - xOff, y + 95); }
        if (node.right != null) { g.setColor(new Color(160,160,160)); g.setStroke(new BasicStroke(1.8f)); g.drawLine(x, y, x + xOff, y + 95); }
        drawBSTNode(g, node.left,  x - xOff, y + 95, Math.max(xOff / 2, 30));
        drawBSTNode(g, node.right, x + xOff, y + 95, Math.max(xOff / 2, 30));
        boolean isRoot = (node == bstRoot);
        g.setColor(isRoot ? new Color(255,210,80) : new Color(255,182,193));
        g.fillOval(x - 36, y - 24, 72, 48);
        g.setColor(isRoot ? new Color(180,120,0) : new Color(180,80,100));
        g.setStroke(new BasicStroke(isRoot ? 3f : 2f));
        g.drawOval(x - 36, y - 24, 72, 48);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(node.name, x - fm.stringWidth(node.name) / 2, y - 3);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String ps = "Rs." + node.price;
        g.setColor(new Color(80,80,80));
        g.drawString(ps, x - fm.stringWidth(ps) / 2 + 2, y + 12);
    }

    // =========================================================
    //  SHOPPING CART  —  Stack
    // =========================================================
    static class CartItem {
        final String name; final int qty; final double price;
        CartItem(String n, int q, double p){name=n;qty=q;price=p;}
        double total(){ return qty*price; }
    }

    static class ShoppingCart {
        private final Stack<CartItem> stack = new Stack<>();
        static final int MAX = 6;
        boolean add(String name, int qty, double price){ if(stack.size()>=MAX)return false; stack.push(new CartItem(name,qty,price)); return true; }
        boolean isEmpty(){ return stack.isEmpty(); }
        boolean isFull() { return stack.size()>=MAX; }
        CartItem[] items(){ return stack.toArray(new CartItem[0]); }
        double grandTotal(){ return stack.stream().mapToDouble(CartItem::total).sum(); }
        void clear(){ stack.clear(); }
    }

    static final String[] STAT_NAMES  = {"Pen","Pencil","Book","Register","Eraser","Sharpener","Copy"};
    static final double[] STAT_PRICES = {6,    1,       6,     1,         8,       5,          9};

    private static void showCart() {
        JFrame frame = new JFrame("Shopping Cart  (Stack — max " + ShoppingCart.MAX + " items)");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        JTextArea avail = new JTextArea();
        avail.setEditable(false);
        avail.setFont(new Font("Monospaced", Font.PLAIN, 14));
        avail.setText("Available items:\n\n");
        for (int i = 0; i < STAT_NAMES.length; i++)
            avail.append(String.format("  %d. %-14s Rs.%.0f%n", i+1, STAT_NAMES[i], STAT_PRICES[i]));

        String[] cols = {"#","Item","Qty","Unit Price","Line Total"};
        DefaultTableModel model = new DefaultTableModel(cols, 0){ @Override public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(model);
        table.setFont(new Font("Monospaced", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel totalLbl = new JLabel("Grand total: Rs.0");
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLbl.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        ctrl.add(label14("Item (1-" + STAT_NAMES.length + "):"));
        JTextField itemF = new JTextField(5); itemF.setFont(new Font("SansSerif",Font.PLAIN,14)); ctrl.add(itemF);
        ctrl.add(label14("Qty:"));
        JTextField qtyF  = new JTextField(5); qtyF.setFont(new Font("SansSerif",Font.PLAIN,14));  ctrl.add(qtyF);
        JButton addBtn   = bigBtn("Add to Cart");  ctrl.add(addBtn);
        JButton clearBtn = bigBtn("Clear Cart");   ctrl.add(clearBtn);

        addBtn.addActionListener(e -> {
            try {
                int choice = Integer.parseInt(itemF.getText().trim()) - 1;
                int qty    = Integer.parseInt(qtyF.getText().trim());
                if (choice<0||choice>=STAT_NAMES.length){err(frame,"Item must be 1–"+STAT_NAMES.length);return;}
                if (qty<=0){err(frame,"Quantity must be ≥ 1.");return;}
                if (sharedCart.isFull()){err(frame,"Cart is full ("+ShoppingCart.MAX+" items max).");return;}
                sharedCart.add(STAT_NAMES[choice], qty, STAT_PRICES[choice]);
                refreshCartTable(model, totalLbl); itemF.setText(""); qtyF.setText("");
            } catch(NumberFormatException ex){err(frame,"Enter valid numbers.");}
        });
        clearBtn.addActionListener(e->{sharedCart.clear();refreshCartTable(model,totalLbl);});

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(avail), new JScrollPane(table));
        split.setDividerLocation(300);

        JPanel south = new JPanel(new BorderLayout());
        south.add(ctrl, BorderLayout.CENTER);
        south.add(totalLbl, BorderLayout.EAST);

        frame.add(split, BorderLayout.CENTER);
        frame.add(south, BorderLayout.SOUTH);
        refreshCartTable(model, totalLbl);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JLabel label14(String text){ JLabel l = new JLabel(text); l.setFont(new Font("SansSerif",Font.PLAIN,14)); return l; }
    private static JButton bigBtn(String text){ JButton b = new JButton(text); b.setFont(new Font("SansSerif",Font.PLAIN,14)); return b; }

    private static void refreshCartTable(DefaultTableModel model, JLabel totalLbl) {
        model.setRowCount(0);
        CartItem[] items = sharedCart.items();
        for (int i = 0; i < items.length; i++) {
            CartItem it = items[i];
            model.addRow(new Object[]{i+1, it.name, it.qty, String.format("Rs.%.0f",it.price), String.format("Rs.%.0f",it.total())});
        }
        totalLbl.setText(String.format("Grand total: Rs.%.0f", sharedCart.grandTotal()));
    }

    // =========================================================
    //  CHECKOUT
    // =========================================================
    private static void showCheckout() {
        JFrame frame = new JFrame("Checkout");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        if (sharedCart.isEmpty()) {
            JLabel empty = new JLabel("Cart is empty! Add items first.", SwingConstants.CENTER);
            empty.setFont(new Font("SansSerif", Font.ITALIC, 18));
            frame.add(empty, BorderLayout.CENTER); frame.setLocationRelativeTo(null); frame.setVisible(true); return;
        }

        String[] cols = {"Item","Qty","Unit Price","Line Total"};
        DefaultTableModel model = new DefaultTableModel(cols, 0){ @Override public boolean isCellEditable(int r,int c){return false;} };
        for (CartItem it : sharedCart.items())
            model.addRow(new Object[]{it.name, it.qty, String.format("Rs.%.0f",it.price), String.format("Rs.%.0f",it.total())});
        model.addRow(new Object[]{"","","TOTAL", String.format("Rs.%.0f",sharedCart.grandTotal())});

        JTable table = new JTable(model);
        table.setFont(new Font("Monospaced", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

        String date = new SimpleDateFormat("dd-MM-yyyy  HH:mm").format(new Date());
        JTextArea header = new JTextArea(
            "===========================================\n" +
            "          DSA Shopping Simulation\n" +
            "          Receipt — " + date + "\n" +
            "===========================================");
        header.setEditable(false);
        header.setFont(new Font("Monospaced", Font.PLAIN, 14));
        header.setBackground(frame.getBackground());

        JLabel totalLbl = new JLabel(String.format("Grand Total: Rs.%.0f", sharedCart.grandTotal()), SwingConstants.RIGHT);
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        totalLbl.setForeground(new Color(0,130,60));
        totalLbl.setBorder(BorderFactory.createEmptyBorder(8,8,8,20));

        JButton confirmBtn = new JButton("Confirm Purchase");
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        confirmBtn.setBackground(new Color(0,150,80));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.addActionListener(e -> {
            sharedCart.clear();
            JOptionPane.showMessageDialog(frame,"Purchase confirmed! Thank you.\nCart has been cleared.","Success",JOptionPane.INFORMATION_MESSAGE);
            frame.dispose();
        });

        JPanel south = new JPanel(new BorderLayout());
        south.add(totalLbl, BorderLayout.CENTER);
        south.add(confirmBtn, BorderLayout.EAST);

        frame.add(header, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(south, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // =========================================================
    //  LIBRARY QUEUE  —  circular queue
    // =========================================================
    static class LibraryQueue {
        private final String[] books;
        private int front, rear, size;
        private final int capacity;

        LibraryQueue() {
            capacity=5; books=new String[capacity]; front=0; rear=-1; size=0;
            enqueue("C++"); enqueue("Physics"); enqueue("History"); enqueue("English"); enqueue("Mathematics");
        }
        boolean enqueue(String b){ if(size==capacity)return false; rear=(rear+1)%capacity; books[rear]=b; size++; return true; }
        String dequeue(){ if(size==0)return null; String b=books[front]; front=(front+1)%capacity; size--; return b; }
        String[] snapshot(){ String[] s=new String[size]; for(int i=0;i<size;i++)s[i]=books[(front+i)%capacity]; return s; }
        boolean isEmpty(){ return size==0; }
    }

    static LibraryQueue libraryQueue = new LibraryQueue();

    private static void showLibraryQueue() {
        libraryQueue = new LibraryQueue();

        JFrame frame = new JFrame("Library Queue  (Circular Queue — FIFO)");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.WHITE);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String[] books = libraryQueue.snapshot();
                int bw=140, bh=90, gap=20;
                int totalW = books.length * bw + (books.length - 1) * gap;
                int sx = (getWidth() - totalW) / 2, y = (getHeight() - bh) / 2;

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("Dequeue from FRONT  —  Enqueue at REAR", 24, 36);

                for (int i = 0; i < books.length; i++) {
                    int x = sx + i * (bw + gap);
                    boolean front = (i==0), rear = (i==books.length-1);
                    g2.setColor(front ? new Color(180,230,180) : rear ? new Color(255,210,150) : new Color(210,210,255));
                    g2.fillRoundRect(x, y, bw, bh, 14, 14);
                    g2.setColor(Color.DARK_GRAY); g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(x, y, bw, bh, 14, 14);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(books[i], x+(bw-fm.stringWidth(books[i]))/2, y+50);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    if (front){ g2.setColor(new Color(0,130,0));  g2.drawString("◄ FRONT", x+20, y+bh+22); }
                    if (rear) { g2.setColor(new Color(160,80,0)); g2.drawString("REAR ►",  x+28, y+bh+22); }
                    if (i < books.length - 1) {
                        int ax = x+bw+gap/2;
                        g2.setColor(Color.GRAY); g2.setStroke(new BasicStroke(1.5f));
                        g2.drawLine(x+bw, y+bh/2, ax+5, y+bh/2);
                        g2.drawLine(ax+5, y+bh/2, ax, y+bh/2-6);
                        g2.drawLine(ax+5, y+bh/2, ax, y+bh/2+6);
                    }
                }
            }
        };

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void showLibraryIssue() {
        JFrame frame = new JFrame("Issue Book from Library");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 14));
        appendBookList(log, libraryQueue);

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        ctrl.add(label14("Book number to issue:"));
        JTextField numF = new JTextField(6); numF.setFont(new Font("SansSerif",Font.PLAIN,14)); ctrl.add(numF);
        JButton issueBtn = bigBtn("Issue Book"); ctrl.add(issueBtn);

        issueBtn.addActionListener(e -> {
            try {
                String[] snap = libraryQueue.snapshot();
                int num = Integer.parseInt(numF.getText().trim());
                if (num<1||num>snap.length){err(frame,"Choose 1–"+snap.length);return;}
                String issued = null;
                for (int i=0;i<snap.length;i++) libraryQueue.dequeue();
                for (int i=0;i<snap.length;i++){ if(i==num-1){issued=snap[i];continue;} libraryQueue.enqueue(snap[i]); }
                String today  = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String dueStr = new SimpleDateFormat("dd-MM-yyyy").format(new Date(new Date().getTime()+7L*24*60*60*1000));
                log.append("\n─── Book Issued ───────────────────\n");
                log.append("Book       : "+issued+"\n");
                log.append("Issue date : "+today+"\n");
                log.append("Due date   : "+dueStr+"\n");
                log.append("───────────────────────────────────\n");
                appendBookList(log, libraryQueue); numF.setText("");
            } catch(NumberFormatException ex){err(frame,"Enter a valid number.");}
        });

        frame.add(new JScrollPane(log), BorderLayout.CENTER);
        frame.add(ctrl, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void appendBookList(JTextArea log, LibraryQueue q) {
        String[] books = q.snapshot();
        if (books.length==0){log.append("Queue is empty.\n");return;}
        log.append("Books in queue:\n");
        for (int i=0;i<books.length;i++) log.append("  "+(i+1)+". "+books[i]+"\n");
    }

    // =========================================================
    //  ICE CREAM SHOP
    // =========================================================
    private static void showIceCream() {
        String[] flavors = {"Chocolate","Vanilla","Strawberry"};
        double[] prices  = {50.0,40.0,60.0};

        JFrame frame = new JFrame("Ice Cream Shop");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        JTextArea menu = new JTextArea();
        menu.setEditable(false);
        menu.setFont(new Font("Monospaced", Font.PLAIN, 15));
        menu.setText("Ice Cream Menu\n");
        menu.append("─────────────────────────────\n");
        for (int i=0;i<flavors.length;i++)
            menu.append(String.format("  %d.  %-14s Rs.%.0f%n",i+1,flavors[i],prices[i]));
        menu.append("─────────────────────────────\n");
        menu.append("(Items added here appear in Checkout)\n");

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        ctrl.add(label14("Flavor (1/2/3):"));
        JTextField flvF = new JTextField(5); flvF.setFont(new Font("SansSerif",Font.PLAIN,14)); ctrl.add(flvF);
        ctrl.add(label14("Quantity:"));
        JTextField qtyF = new JTextField(5); qtyF.setFont(new Font("SansSerif",Font.PLAIN,14)); ctrl.add(qtyF);
        JButton addBtn = bigBtn("Add to Cart"); ctrl.add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                int choice = Integer.parseInt(flvF.getText().trim())-1;
                int qty    = Integer.parseInt(qtyF.getText().trim());
                if(choice<0||choice>=flavors.length){err(frame,"Choose 1, 2 or 3.");return;}
                if(qty<=0){err(frame,"Quantity must be ≥ 1.");return;}
                if(sharedCart.isFull()){err(frame,"Cart is full ("+ShoppingCart.MAX+" items).");return;}
                sharedCart.add(flavors[choice]+" Ice Cream",qty,prices[choice]);
                menu.append(String.format("Added: %s x%d = Rs.%.0f%n",flavors[choice],qty,prices[choice]*qty));
                flvF.setText(""); qtyF.setText("");
            } catch(NumberFormatException ex){err(frame,"Enter valid numbers.");}
        });

        frame.add(new JScrollPane(menu), BorderLayout.CENTER);
        frame.add(ctrl, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // =========================================================
    //  UTILITY
    // =========================================================
    private static void err(JFrame parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}