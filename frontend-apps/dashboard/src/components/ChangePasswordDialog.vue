<template>
  <el-dialog
    title="修改密码"
    v-model="visible"
    width="400px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      status-icon
    >
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input
          v-model="form.oldPassword"
          type="password"
          placeholder="请输入旧密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          确认
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { updatePassword } from "@/apis/auth";
import { useAuthStore } from "@/stores/auth";

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: "update:modelValue", value: boolean): void;
  (e: "success"): void;
}>();

const visible = ref(false);
const loading = ref(false);
const formRef = ref<FormInstance>();
const authStore = useAuthStore();

const form = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const validatePass2 = (rule: any, value: string, callback: any) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== form.newPassword) {
    callback(new Error("两次输入密码不一致!"));
  } else {
    callback();
  }
};

const rules = reactive<FormRules>({
  oldPassword: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: "blur" },
  ],
});

watch(
  () => props.modelValue,
  (val) => {
    visible.value = val;
    if (val) {
      form.oldPassword = "";
      form.newPassword = "";
      form.confirmPassword = "";
    }
  }
);

watch(
  () => visible.value,
  (val) => {
    emit("update:modelValue", val);
  }
);

const handleClose = () => {
  formRef.value?.resetFields();
};

const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        if (!authStore.user?.userId) {
          ElMessage.error("用户信息获取失败");
          return;
        }

        await updatePassword({
          adminId: authStore.user.userId,
          oldPassword: form.oldPassword,
          newPassword: form.newPassword,
        });

        ElMessage.success("密码修改成功，请重新登录");
        visible.value = false;
        emit("success");

        // 密码修改成功后退出登录
        await authStore.logout();
        window.location.reload();
      } catch (error: any) {
        console.error("修改密码失败:", error);
        // 错误处理已经在 request.ts 中统一处理了，或者在这里补充
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>
