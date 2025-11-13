package resto.tda;

public class GraphWeightedMatrix {
    private final String[] names;
    private final int[][] w; //w[i][j] = metros (>0) o 0 si no hay calle
    private int n = 0;

    public GraphWeightedMatrix(int maxVertices) {
        this.names = new String[maxVertices];
        this.w = new int[maxVertices][maxVertices];
    }

    public int addVertex(String name) { names[n] = name; return n++; }

    public void addEdgeMeters(int from, int to, int meters) {
        w[from][to] = meters; w[to][from] = meters;
    }

    public int weight(int from, int to) { int d=w[from][to]; return d==0? -1:d; }
    public int indexOf(String name){ for(int i=0;i<n;i++) if(names[i].equals(name)) return i; return -1; }
    public int size(){ return n; }
    public String nameOf(int idx){ return names[idx]; }

    /*BFS en metros mediante expansión a sub-aristas de longitud "unit" (p.ej., 10 m)
    Requiere que todos los pesos sean múltiplos de unit. Retorna distancia en metros o -1 si no hay camino.
    Retorna -2 si algún peso no es múltiplo exacto de unit.
    */

    public int bfsDistanceMeters(int from, int to, int unit) {
        int extra = 0;
        for(int i=0;i<n;i++){
            for(int j=i+1;j<n;j++){
                int meters = w[i][j];
                if(meters>0){
                    int k = meters/unit;
                    if(k<=0 || k*unit!=meters) return -2;
                    extra += (k-1);
                }
            }
        }
        int N = n + extra;
        byte[][] adj = new byte[N][N];

        int next = n;
        for(int i=0;i<n;i++){
            for(int j=i+1;j<n;j++){
                int meters = w[i][j];
                if(meters>0){
                    int k = meters/unit;
                    if(k==1){
                        adj[i][j]=1; adj[j][i]=1;
                    }else{
                        int prev = i;
                        for(int t=1;t<=k-1;t++){
                            int cur = (t==k-1)? j : next++;
                            adj[prev][cur]=1; adj[cur][prev]=1;
                            prev = cur;
                        }
                    }
                }
            }
        }

        boolean[] vis = new boolean[N];
        int[] dist = new int[N];
        int[] q = new int[N];
        int qs=0, qe=0;
        vis[from]=true; dist[from]=0; q[qe++]=from;

        while(qs<qe){
            int v=q[qs++];
            if(v==to) return dist[v]*unit;
            for(int u=0;u<N;u++){
                if(adj[v][u]==1 && !vis[u]){
                    vis[u]=true;
                    dist[u]=dist[v]+1;
                    q[qe++]=u;
                }
            }
        }
        return -1;
    }
}
