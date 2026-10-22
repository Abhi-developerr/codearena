
export const LANGUAGES = [
  {
    label: "Java",
    value: "JAVA",
    editorLanguage: "java",
    defaultCode: `class Solution {
    public int[] twoSum(int[] nums, int target) {
        // Write your solution here
        return new int[]{};
    }
}`,
  },
  {
    label: "Python",
    value: "PYTHON",
    editorLanguage: "python",
    defaultCode: `class Solution:
    def twoSum(self, nums, target):
        # Write your solution here
        pass`,
  },
  {
    label: "C++",
    value: "CPP",
    editorLanguage: "cpp",
    defaultCode: `class Solution {
public:
    vector<int> twoSum(vector<int>& nums, int target) {
        // Write your solution here
        return {};
    }
};`,
  },
];

export function getLanguageConfig(value) {
  return LANGUAGES.find((language) => language.value === value)
    ?? LANGUAGES[0];
}
