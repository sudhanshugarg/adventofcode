#include<iostream>
#include<map>
using namespace std;

string text_to_digit(const string &s, map<int, string> &m) {
  string res = "";
  bool flag;
  for (int i = 0; i < s.length(); ) {
    flag = true;
    for (int j = 1; flag && j < 10; j++) {
      if ((i + m[j].length()) > s.length()) continue;
      if (s.substr(i, m[j].length()) == m[j]) {
        flag = false;
        //i += m[j].length();
        i++;
        res += (j + '0');
      }
    }
    if (flag) {
        res += s[i];
        i++;
    }
  }
  return res;
}

int calibrate(const string &s) {
  bool flag = false;
  int start = 0, end = 0;
  for (int i = 0; i < s.length(); i++) {
    if (s[i] >= '0' && s[i] <= '9') {
      end = s[i] - '0';
      if (!flag) {
        start = s[i] - '0';
        flag = true;
      }
    }
  }
  return start * 10 + end;
}

int main() {
  string raw, s;
  int total = 0, num;
  map<int, string> m;
  m[1] = "one";
  m[2] = "two";
  m[3] = "three";
  m[4] = "four";
  m[5] = "five";
  m[6] = "six";
  m[7] = "seven";
  m[8] = "eight";
  m[9] = "nine";
  while (cin >> raw) {
    s = text_to_digit(raw, m);
    num = calibrate(s);
    total += num;
  }
  cout << total << endl;
}
