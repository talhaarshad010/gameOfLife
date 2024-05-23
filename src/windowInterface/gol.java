package windowInterface;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class gol extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int GRID_SIZE = 50;
    private static final int CELL_SIZE = WIDTH / GRID_SIZE;

    public gol() {
        setTitle("Conway's Game of Life");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the grid panel
        GridPanel gridPanel = new GridPanel(GRID_SIZE, CELL_SIZE);
        add(gridPanel);

        // Start the game
        Timer timer = new Timer(100, e -> {
            gridPanel.updateGrid();
            gridPanel.repaint();
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gol frame = new gol();
            frame.setVisible(true);
        });
    }
}

class GridPanel extends JPanel {
    private final int gridSize;
    private final int cellSize;
    private boolean[][] cells;

    public GridPanel(int gridSize, int cellSize) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.cells = new boolean[gridSize][gridSize];
        initializeGrid();
    }

    private void initializeGrid() {
        Random random = new Random();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                cells[i][j] = random.nextBoolean();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (cells[i][j]) {
                    g.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
                } else {
                    g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    public void updateGrid() {
        boolean[][] newCells = new boolean[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int aliveNeighbors = countAliveNeighbors(i, j);
                if (cells[i][j]) {
                    newCells[i][j] = aliveNeighbors == 2 || aliveNeighbors == 3;
                } else {
                    newCells[i][j] = aliveNeighbors == 3;
                }
            }
        }
        cells = newCells;
    }

    private int countAliveNeighbors(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int nx = x + i;
                int ny = y + j;
                if (nx >= 0 && ny >= 0 && nx < gridSize && ny < gridSize) {
                    if (cells[nx][ny]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
