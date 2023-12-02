#include<iostream>
#include<sstream>
using namespace std;

bool isNumeric(char a) {
  return a >= '0' && a <= '9';
}

void myp(string s) {
  cout << "#" << s << "#" << endl;
}

void parseTurn(string s, bool &possible, int rgb[3]) {
  int i = 0;
  while (i < s.length()) {
    int j = i;
    while(isNumeric(s[j])) j++;
    istringstream iss(s.substr(i, j-i));
    int num;
    iss >> num;
    j++;
    int k = j;
    while(k < s.length() && (s[k] != ',')) k++;
    string color = s.substr(j, k-j);
    //if (color == "red" && num > 12) possible = false;
    //else if (color == "green" && num > 13) possible = false;
    //else if (color == "blue" && num > 14) possible = false;
    i = k + 2;

    if (color == "red" && rgb[0] < num) rgb[0] = num;
    else if (color == "green" && rgb[1] < num) rgb[1] = num;
    else if (color == "blue" && rgb[2] < num) rgb[2] = num;
    
  }
}

void parseGame(string &game, int &id, bool &possible, long long &power) {
  possible = true;
  int j = 5;
  while(isNumeric(game[j])) j++;
  int rgb[3];
  rgb[0] = rgb[1] = rgb[2] = 0;

  istringstream iss(game.substr(5, j-5));
  iss >> id;
  j += 2;

  while(j < game.length()) {
    int i = j;
    while(i < game.length() && game[i] != ';') i++;
    parseTurn(game.substr(j, i-j), possible, rgb);
    //if (!possible) return;
    j = i+2;
  }

  power = rgb[0] * rgb[1] * rgb[2];
}


int main() {
  string game;
  int total = 0;
  int id;
  bool possible;
  long long power = 0;
  while(getline(std::cin, game)) {
    parseGame(game, id, possible, power);
    cout << id << " : " << power << endl;
    total += power;
  }
  cout << total << endl;
  return 1;
}
