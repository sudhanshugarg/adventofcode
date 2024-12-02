#include<iostream>
#include<vector>
#include<set>
#include<sstream>

using namespace std;
typedef long long LL;

vector<vector<LL> > seedSoil, soilFertilizer, fertilizerWater, waterLight, lightTemp, tempHumidity, humidityLoc;
vector<vector<string> > input(7, vector<string>());
int curr = -1;
LL maxes = -1;

void printStringVector(vector<string> &vs) {
  for (int i = 0; i < vs.size(); i++) {
    cout << vs[i] << endl;
  }
  cout << endl;
}

void printV(vector<LL> &v) {
  printf("start %lld, end %lld\n", v[0], v[1]);
}

void printVV(vector<vector<LL> > &v, string name) {
  cout << name << "," << v.size() << endl;
  for (int i = 0; i < v.size(); i++) {
    printf("dest %lld, src %lld, ct %lld\n", v[i][0], v[i][1], v[i][2]);
  }
  cout << endl;
}


void findMultiMappingHelper(vector<LL> srcToDest, vector<vector<LL> > &mapping, vector<vector<LL> > &splits, string who) {
  //base case
  if (srcToDest[0] > srcToDest[1]) return;
  cout << endl << "starting check for: ";
  printV(srcToDest);
  cout << "in findMultiMappingHelper: " << who << endl;

  bool isStart, isEnd;
  bool anyOverlap = false;
  for (int i = 0; i < mapping.size(); i++) {
    LL srcStart = mapping[i][1]; //0
    LL srcEnd = mapping[i][1] + mapping[i][2] - 1; //15
    cout << "checking mapping srcStart: " << srcStart << ", srcEnd: " << srcEnd << endl;

    //check basic case, if entire range is inside this mapping.
    if (srcToDest[0] >= srcStart && srcToDest[1] <= srcEnd) { // 5 >= 0 && 8 <= 15
      cout << "reached here" << endl;
      vector<LL> nextSplit(2, 0);
      nextSplit[0] = srcToDest[0] + mapping[i][0] - mapping[i][1];
      nextSplit[1] = srcToDest[1] + mapping[i][0] - mapping[i][1];
      cout << "added "; printV(nextSplit);
      splits.push_back(nextSplit);
      return;
    }

    isStart = isEnd = false;
    if (srcStart >= srcToDest[0] && srcStart <= srcToDest[1]) {
      isStart = true;
      anyOverlap = true;
      cout << "start is true: " << srcStart << endl;
      printV(srcToDest);
    }
    if (srcEnd >= srcToDest[0] && srcEnd <= srcToDest[1]) {
      isEnd = true;
      anyOverlap = true;
      cout << "end is true: " << srcEnd << endl;
      printV(srcToDest);
    }

    if (isStart && isEnd) {
      vector<LL> nextSplit(2, 0);
      nextSplit[0] = mapping[i][0];
      nextSplit[1] = mapping[i][0] + mapping[i][2] - 1;
      splits.push_back(nextSplit);

      vector<LL> left(2, 0), right(2, 0);
      left[0] = srcToDest[0];
      left[1] = mapping[i][1] - 1;
      findMultiMappingHelper(left, mapping, splits, "both");

      right[0] = mapping[i][1] + mapping[i][2];
      right[1] = srcToDest[1];
      findMultiMappingHelper(right, mapping, splits, "both");
      break;
    } else if (isStart) {
      vector<LL> nextSplit(2, 0);
      nextSplit[0] = mapping[i][0];
      nextSplit[1] = mapping[i][0] + (srcToDest[1] - mapping[i][1]);
      splits.push_back(nextSplit);

      vector<LL> left(2, 0);
      left[0] = srcToDest[0];
      left[1] = mapping[i][1] - 1;
      findMultiMappingHelper(left, mapping, splits, "start");
      break;

    } else if (isEnd) {
      vector<LL> nextSplit(2, 0);
      nextSplit[0] = mapping[i][0] + (srcToDest[0] - mapping[i][1]);
      nextSplit[1] = mapping[i][0] + mapping[i][2] - 1;
      splits.push_back(nextSplit);

      vector<LL> right(2, 0);
      right[0] = mapping[i][1] + mapping[i][2];
      right[1] = srcToDest[1];
      findMultiMappingHelper(right, mapping, splits, "end");
      break;
    }
  }

  if (!anyOverlap) {
    splits.push_back(srcToDest);
  }
  cout << "curr splits are: "; printVV(splits, "splits");
}

vector<vector<LL> > findMultiMapping(vector<vector<LL> > &srcToDests, vector<vector<LL> > &mapping, int which) {
  cout << "findMultiMapping:" << which << endl;

  vector<vector<LL> > splits;
  splits.clear();
  for (int i = 0; i < srcToDests.size(); i++) {
    findMultiMappingHelper(srcToDests[i], mapping, splits, "root");
  }
  cout << "findMultiMapping:" << which << " done" << endl;
  return splits;
}

LL findMinSrc(vector<vector<LL> > &ranges) {
  printVV(ranges, "all");
  LL minLoc = ranges[0][0];
  for (int i = 1; i < ranges.size(); i++)
    minLoc = minLoc > ranges[i][0] ? ranges[i][0] : minLoc;
  return minLoc;
}

LL findMapping(LL &src, vector<vector<LL> > mapping) {
  LL dest = src;
  for (int i = 0; i < mapping.size(); i++) {
    if (src >= mapping[i][1] && src < (mapping[i][1] + mapping[i][2])) {
      dest = mapping[i][0] + (src - mapping[i][1]);
      break;
    }
  }
  return dest;
}

void parseSeeds(string &line, vector<LL> &seeds) {
  istringstream iss(line.substr(7));
  LL next;
  while(iss >> next) seeds.push_back(next);
}

void inc() {
  curr++;
}

void parseMap(vector<vector<LL> > &res, vector<string> lines) {
  LL src, dest, ct;
  for (int i = 0; i < lines.size(); i++) {
    istringstream iss(lines[i]);
    iss >> dest >> src >> ct;
    maxes = maxes < dest ? dest : maxes;
    maxes = maxes < src ? src : maxes;
    maxes = maxes < ct ? ct : maxes;
    vector<LL> v;
    v.push_back(dest);
    v.push_back(src);
    v.push_back(ct);
    res.push_back(v);
  }
}

int main() {
  string line;
  vector<LL> seeds;
  while(getline(std::cin, line)) {
    if (line.substr(0,6) == "seeds:") parseSeeds(line, seeds);
    else if (line == "seed-to-soil map:") inc();
    else if (line == "soil-to-fertilizer map:") inc();
    else if (line == "fertilizer-to-water map:") inc();
    else if (line == "water-to-light map:") inc();
    else if (line == "light-to-temperature map:") inc();
    else if (line == "temperature-to-humidity map:") inc();
    else if (line == "humidity-to-location map:") inc();
    else {
      if (line.size() > 0) input[curr].push_back(line);
    }
  }

  parseMap(seedSoil, input[0]);
  parseMap(soilFertilizer, input[1]);
  parseMap(fertilizerWater, input[2]);
  parseMap(waterLight, input[3]);
  parseMap(lightTemp, input[4]);
  parseMap(tempHumidity, input[5]);
  parseMap(humidityLoc, input[6]);

  //cout << "num seeds: " << seeds.size() << endl;
  LL minLoc = -1, nextLoc;
  for (int i = 0; i < seeds.size(); i++) {
    //cout << seeds[i] << ",";
    nextLoc = findMapping(seeds[i], seedSoil);
    nextLoc = findMapping(nextLoc, soilFertilizer);
    nextLoc = findMapping(nextLoc, fertilizerWater);
    nextLoc = findMapping(nextLoc, waterLight);
    nextLoc = findMapping(nextLoc, lightTemp);
    nextLoc = findMapping(nextLoc, tempHumidity);
    nextLoc = findMapping(nextLoc, humidityLoc);
    //cout << nextLoc << endl;
    if ((minLoc == -1) || minLoc > nextLoc) {
      minLoc = nextLoc;
    }
  }
  cout << "ans 1: " << minLoc << endl << endl;

  //part 2
  minLoc = nextLoc = -1;
  for (LL i = 0; i < seeds.size(); i+=2) {
    vector<LL> firstSeed;
    firstSeed.push_back(seeds[i]);
    firstSeed.push_back(seeds[i] + seeds[i+1] - 1);
    vector<vector<LL> > firstSeedInput, nextRange;
    firstSeedInput.push_back(firstSeed);

    nextRange = findMultiMapping(firstSeedInput, seedSoil, 0);
    nextRange = findMultiMapping(nextRange, soilFertilizer, 1);
    nextRange = findMultiMapping(nextRange, fertilizerWater, 2);
    nextRange = findMultiMapping(nextRange, waterLight, 3);
    nextRange = findMultiMapping(nextRange, lightTemp, 4);
    nextRange = findMultiMapping(nextRange, tempHumidity, 5);
    nextRange = findMultiMapping(nextRange, humidityLoc, 6);
    //printVV(nextRange, oss.str());
    nextLoc = findMinSrc(nextRange);
    if ((minLoc == -1) || minLoc > nextLoc) {
      minLoc = nextLoc;
    }
  }
  cout << endl << "ans 2: " << minLoc << endl;

  /*
  printVV(seedSoil, "seedSoil");
  printVV(soilFertilizer, "soilFertilizer");
  printVV(fertilizerWater, "fertilizerWater");
  printVV(waterLight, "waterLight");
  printVV(lightTemp, "lightTemp");
  printVV(tempHumidity, "tempHumidity");
  printVV(humidityLoc, "humidityLoc");
  */
  return 0;
}
