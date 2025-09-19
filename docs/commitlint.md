# commitlint说明文档

## 什么是commitlint？

- [commitlint](https://commitlint.js.org/#/)是一款用于校验和规范化`git commit message`的工具，它通过定制化的规则要求提交信息遵循特定格式，可以在`commit`时自动进行校验，拒绝不合规范的提交，以此提高提交信息质量，使其更具可读性和一致性。

## 配置文件是哪个？

- 见.commitlintrc.json。

## 什么是@commitlint/config-conventional？

- 它是一组常见配置的规则集合，其具体配置可见[该链接](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional)中的相关描述。

## commitlint如何与husky配合使用？

- 由于`commitlint`需要获取用户的`commit message`输入，因此它将检查命令放在[git钩子](https://git-scm.com/docs/githooks)的`commit-msg`钩子中，见`.husky/commit-msg`。

## 提交说明(commit message)

### 格式

提交说明(commit message)的结构如下所示：

```
<type>([optional scope]): <description>

[optional body]

[optional footer(s)]
```

### 提交类型

提交说明(commit message)包含了下面的结构化元素，以向类库使用者表明其意图：

- `fix`：表示在代码库中修复了一个 bug（这和语义化版本中的 **PATCH** 相对应）。
- `feat`：表示在代码库中新增了一个功能（这和语义化版本中的 **MINOR** 相对应）。
- `BREAKING CHANGE`：在 `footer` 中包含 `BREAKING CHANGE: ` 或 `type(scope)` 后面有一个 `!` 的提交，表示引入了破坏性 API 变更（这和语义化版本中的 **MAJOR** 相对应）。 破坏性变更可以是任意提交类型的一部分。

除 `fix` 和 `feat` 之外，也可以使用其它提交类型，如下：

- `build`：与构建系统或外部依赖相关的更改，例如修改构建脚本，更新依赖库等
- `chore`：常规的代码维护和小修改，不属于应用程序逻辑或业务特性的更改
- `ci`：与持续集成（Continuous Integration）和持续交付（Continuous Delivery）系统有关的更改
- `docs`：仅与文档相关的更改，例如修改README，添加注释等
- `feat`：引入新特性的更改，通常是添加新功能或实现新需求
- `fix`：修复bug或错误的更改，用于解决代码中的问题或缺陷
- `perf`：优化性能的更改，用于提高应用程序的运行效率和速度
- `refactor`：重构代码的更改，不涉及修复bug或添加新功能，主要是为了改进代码的结构和可读性
- `revert`：撤销先前提交的更改，用于回滚代码到某个特定的版本
- `style`：与代码风格和格式有关的更改，不影响代码的逻辑和功能
- `test`：与测试有关的更改，包括添加新测试，修改现有测试或修复测试bug
