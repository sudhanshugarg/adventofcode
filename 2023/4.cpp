#include<iostream>
#include<vector>
#include<set>
#include<sstream>

using namespace std;

void printset(set<int> &s) {
  set<int>::iterator it;
  for (it = s.begin(); it != s.end(); it++) cout << *it << ",";
  cout << endl;
}

void compute(string &card, long long &val, int &matches, int &game_id) {
  val = 0;
  int colon, pipe;
  for (int i = 0; i < card.length(); i++) {
    if (card[i] == ':') colon = i;
    else if (card[i] == '|') {
      pipe = i;
      break;
    }
  }
  //cout << colon << "," << pipe << endl;
  string winning = card.substr(colon+2, pipe - colon - 2);
  string mine = card.substr(pipe + 2);

  //cout << winning << " # " << mine << endl;

  int next;
  set<int> winset, myset;

  istringstream w_iss(winning);
  while(w_iss >> next) {
    winset.insert(next);
  }

  istringstream m_iss(mine);
  while(m_iss >> next) {
    myset.insert(next);
  }

  //if (winset.size() != 10) cout << winning << " winset is not 10 " << endl;
  //if (myset.size() != 25) cout << mine << " myset is not 25 " << endl;

  //printset(winset);
  //printset(myset);

  matches = 0;
  set<int>::iterator it;
  for (it = winset.begin(); it != winset.end(); it++) {
    if (myset.find(*it) != myset.end()) matches++;
  }
  //cout << matches << endl;

  if (matches > 0) val = (long long) pow(2.0, matches - 1);
  //cout << val << endl;

  string id = card.substr(5, colon - 5);
  istringstream id_iss(id);
  id_iss >> game_id;




}

int main() {
  string card;
  long long total = 0;
  long long val;
  int matches, game_id;
  vector<int> counts(210, 1);
  int max_game_id = 0;
  while(getline(std::cin, card)) {
    compute(card, val, matches, game_id);
    max_game_id = max_game_id < game_id ? game_id : max_game_id;
    //cout << val << endl;
    //total += val;
    for (int i = 0; i < matches; i++) {
      counts[game_id + 1 + i] += counts[game_id];
    }
  }

  for (int i = 1; i <= max_game_id; i++) {
    cout << i << ":" << counts[i] << endl;
    total += counts[i];
  }
  cout << total << endl;
}
