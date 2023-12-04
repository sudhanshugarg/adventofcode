#include<iostream>
#include<sstream>
#include<vector>
using namespace std;

int dirs[8][2] = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}, {-1, 1}, {-1, -1}, {1, -1}, {1, 1}};
int lr[2][2] = {{0, 1}, {0, -1}};
int top[1][2] = {{-1, 0}};
int bot[1][2] = {{1, 0}};
int topdiag[2][2] = {{-1, 1}, {-1, -1}};
int botdiag[2][2] = {{1, 1}, {1, -1}};

vector<vector<int> > lrv, topv, botv, topdiagv, botdiagv;

bool isNumeric(char a) {
  return a >= '0' && a <= '9';
}

bool isNotSymbol(char a) {
  return isNumeric(a) || a == '.';
}

void findFromOne(vector<string> &input, int a, int b, long long &next, int n, bool &p) {
  if (p) {
    cout << a << "," << b << endl;
  }
  int left, right;
  left = b;
  while(left >= 0 && isNumeric(input[a][left])) left--;
  left++;

  right = b;
  while(right < n && isNumeric(input[a][right])) right++;
  if (p) cout << left << "," << right << "#" << endl;
  istringstream iss(input[a].substr(left, right-left));
  iss >> next;
}

void findAndAdd(vector<string> &input, int a, int b, int m, int n, vector<vector<int> > &dir, bool &flag, vector<long long> &adj, bool p) {
  for (int d = 0; d < dir.size(); d++) {
    int nextr = a+dir[d][0];
    int nextc = b+dir[d][1];
    if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n) continue;
    if (!isNumeric(input[nextr][nextc])) continue;
    flag = true;

    long long next = 0;
    findFromOne(input, nextr, nextc, next, n, p);
    adj.push_back(next);
  }
}

void findAdjacentNumbers(vector<string> &input, int a, int b, vector<long long> &adj) {
  int m = input.size();
  int n = input[0].length();

  bool found;
  findAndAdd(input, a, b, m, n, lrv, found, adj, false);

  bool isTop = false;
  findAndAdd(input, a, b, m, n, topv, isTop, adj, false);
  
  if (!isTop) {
    findAndAdd(input, a, b, m, n, topdiagv, isTop, adj, false);
  }

  bool isBot = false;
  findAndAdd(input, a, b, m, n, botv, isBot, adj, false);
  
  if (!isBot) {
    findAndAdd(input, a, b, m, n, botdiagv, isBot, adj, false);
  }
}


void findGearRatio(vector<string> &input, vector<long long> &gears) {
  int m = input.size();
  int n = input[0].length();
  for (int i = 0; i < m; i++) {
    for (int j = 0; j < n; j++) {
      if (input[i][j] == '*') {
        //cout << i << "," << j << ", asterisk" << endl;
        vector<long long> adj;
        findAdjacentNumbers(input, i, j, adj);
        if (adj.size() == 2) gears.push_back(adj[0] * adj[1]);
        adj.clear();
      }
    }
  }
}

bool isEnginePart(vector<string>&input, int i, int j, int k, int &next, int m, int n) {
  bool flag = false;
  for (int a = j; a < k; a++) {
    for (int d = 0; d < 8; d++) {
      int nextr = i + dirs[d][0];
      int nextc = a + dirs[d][1];
      if (nextr < 0 || nextr >= m || nextc < 0 || nextc >= n) continue;
      if (isNotSymbol(input[nextr][nextc])) continue;

      flag = true;
      break;
    }
  }
  if (flag) {
    istringstream iss(input[i].substr(j, k-j));
    iss >> next;
  }
  return flag;
}

void findNumbers(vector<string>&input, vector<int> &nums) {
  int m = input.size();
  int n = input[0].length();

  for (int i = 0; i < m; i++) {
    //at row i
    for (int j = 0; j < n;) {
      if (isNumeric(input[i][j])) {
        int k = j;
        while(isNumeric(input[i][k]) && k < n) k++;

        //from j to k check
        int next;
        if (isEnginePart(input, i, j, k, next, m, n)) nums.push_back(next);
        j = k;
      } else {
        j++;
      }
    }
  }
}

int main() {
  string line;
  vector<string> input;
  while(getline(std::cin, line)) {
    input.push_back(line);
  }
  
  vector<int> nums;
  findNumbers(input, nums);
  int total = 0;
  for (int i = 0; i < nums.size(); i++) total += nums[i];
  cout << "part 1 " << total << endl;


  lrv.push_back(vector<int> (begin(lr[0]), end(lr[0])));
  lrv.push_back(vector<int> (begin(lr[1]), end(lr[1])));
  topv.push_back(vector<int> (begin(top[0]), end(top[0])));
  botv.push_back(vector<int> (begin(bot[0]), end(bot[0])));
  topdiagv.push_back(vector<int> (begin(topdiag[0]), end(topdiag[0])));
  topdiagv.push_back(vector<int> (begin(topdiag[1]), end(topdiag[1])));
  botdiagv.push_back(vector<int> (begin(botdiag[0]), end(botdiag[0])));
  botdiagv.push_back(vector<int> (begin(botdiag[1]), end(botdiag[1])));

  vector<long long> gears;
  findGearRatio(input, gears);
  long long sumGearRatio = 0;
  for (int i = 0; i < gears.size(); i++) {
      cout << gears[i] << ",";
      sumGearRatio += gears[i];
  }
  cout << endl << "part 2 " << sumGearRatio << endl;

  return 1;
}
