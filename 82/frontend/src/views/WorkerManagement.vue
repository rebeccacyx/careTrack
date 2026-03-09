<template>
  <div class="worker-management-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Worker Management</h2>
          <a-tooltip title="Care staff management page, Managers can invite Workers to join the team" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
      
      <!-- Action Buttons -->
      <div style="margin-bottom: 20px; display: flex; gap: 12px; flex-wrap: wrap;">
        <div style="display: flex; align-items: center; gap: 4px;">
          <a-button type="primary" @click="generateInviteToken" :loading="generatingToken">
            <template #icon>
              <UserAddOutlined />
            </template>
            Generate Invite Token
          </a-button>
          <a-tooltip title="Generate a unique invitation code for workers to join the care team. The token will expire after the specified number of days." placement="top">
            <span style="color: #999; cursor: help; font-size: 12px; width: 16px; height: 16px; border-radius: 50%; border: 1px solid #999; display: inline-flex; align-items: center; justify-content: center; line-height: 1;">?</span>
          </a-tooltip>
        </div>
        
        <div style="display: flex; align-items: center; gap: 4px;">
          <a-button type="default" @click="showDailyManagementModal">
            <template #icon>
              <CalendarOutlined />
            </template>
            Daily Management
          </a-button>
          <a-tooltip title="Manage daily work schedules and upload attendance photos. Set which workers are assigned for each day and upload their daily photos for verification." placement="top">
            <span style="color: #999; cursor: help; font-size: 12px; width: 16px; height: 16px; border-radius: 50%; border: 1px solid #999; display: inline-flex; align-items: center; justify-content: center; line-height: 1;">?</span>
          </a-tooltip>
        </div>
        
        <div style="display: flex; align-items: center; gap: 4px;">
          <a-button type="default" @click="showShiftTimeSettingsModal">
            <template #icon>
              <ClockCircleOutlined />
            </template>
            Shift Time Settings
          </a-button>
          <a-tooltip title="Configure shift start and end times. These settings will be used as defaults for all future schedules." placement="top">
            <span style="color: #999; cursor: help; font-size: 12px; width: 16px; height: 16px; border-radius: 50%; border: 1px solid #999; display: inline-flex; align-items: center; justify-content: center; line-height: 1;">?</span>
          </a-tooltip>
        </div>
        
      </div>

      <!-- Invite Token Display -->
      <a-alert
        v-if="generatedToken"
        :message="`Generated Invite Token: ${generatedToken}`"
        type="success"
        show-icon
        closable
        @close="generatedToken = null"
        style="margin-bottom: 20px;"
      />

      <!-- Daily Workers Section -->
      <div style="margin-bottom: 30px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h3 style="margin: 0; color: #1890ff;">
            <UserOutlined style="margin-right: 8px;" />
            Daily Workers - {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}
          </h3>
          <a-date-picker
            v-model:value="selectedDate"
            @change="onDateChange"
            style="width: 200px;"
            placeholder="Select date"
          />
        </div>
        
        <!-- Shift Selection -->
        <div style="margin-bottom: 20px;">
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
            <span style="font-weight: 500; color: #666;">Select Shift:</span>
            <a-radio-group v-model:value="selectedShift" @change="onShiftChange" button-style="solid">
              <a-radio-button value="morning">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Morning Shift
              </a-radio-button>
              <a-radio-button value="afternoon">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Afternoon Shift
              </a-radio-button>
              <a-radio-button value="evening">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Evening Shift
              </a-radio-button>
            </a-radio-group>
          </div>
          
          <!-- Shift Info -->
          <div style="background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px; padding: 12px; margin-bottom: 16px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <InfoCircleOutlined style="color: #52c41a;" />
              <span style="font-weight: 500; color: #52c41a;">{{ getShiftInfo().title }}</span>
            </div>
            <div style="margin-top: 4px; font-size: 12px; color: #666;">
              {{ getShiftInfo().description }}
            </div>
          </div>
        </div>
        
        <a-row :gutter="[16, 16]">
          <a-col
            v-for="worker in getFilteredDailyWorkers()"
            :key="worker.id"
            :xs="12"
            :sm="8"
            :md="6"
            :lg="4"
            :xl="3"
          >
            <a-card
              hoverable
              size="small"
              style="text-align: center; height: 140px; cursor: pointer;"
              @click="showWorkerDetail(worker)"
              :class="{ 'selected-worker': selectedWorker?.id === worker.id }"
            >
              <div style="display: flex; flex-direction: column; align-items: center; height: 100%; justify-content: space-between;">
                <!-- Photo -->
                <div style="margin-top: 8px;">
                  <a-avatar
                    :size="50"
                    :src="worker.photo || worker.photoUrl"
                    :alt="worker.name"
                    style="border: 2px solid #f0f0f0;"
                    @error="() => { worker.photo = null; worker.photoUrl = null; }"
                  >
                    {{ worker.name.charAt(0).toUpperCase() }}
                  </a-avatar>
                </div>
                
                <!-- Worker ID -->
                <div style="font-weight: bold; color: #1890ff; font-size: 12px;">
                  {{ worker.workerId }}
                </div>
                
                <!-- Name -->
                <div style="font-size: 11px; color: #666; margin-bottom: 8px; line-height: 1.2;">
                  {{ worker.name }}
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
        
        <!-- No Workers Message -->
        <div v-if="getFilteredDailyWorkers().length === 0" style="text-align: center; padding: 40px; color: #999;">
          <UserOutlined style="font-size: 48px; margin-bottom: 16px;" />
          <div style="font-size: 16px; margin-bottom: 8px;">No workers assigned</div>
          <div style="font-size: 12px;">No workers are scheduled for {{ getShiftInfo().title }} on {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}</div>
        </div>
      </div>

      <!-- All Workers Table -->
      <div style="margin-bottom: 20px;">
        <h3 style="margin-bottom: 16px; color: #52c41a;">
          <TeamOutlined style="margin-right: 8px;" />
          All Workers
        </h3>
        <a-table
          :columns="workerColumns"
          :data-source="workers"
          :pagination="{ pageSize: 10 }"
          row-key="id"
          :loading="loading"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'photo'">
              <a-avatar
                :size="40"
                :src="record.photo || record.photoUrl"
                :alt="record.name"
                @error="() => { record.photo = null; record.photoUrl = null; }"
              >
                {{ record.name?.charAt(0)?.toUpperCase() || 'W' }}
              </a-avatar>
            </template>
            
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="editWorker(record)">
                  Edit
                </a-button>
                <a-button size="small" danger @click="showRemoveConfirmation(record)">
                  Remove
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- Generate Token Modal -->
    <a-modal
      v-model:open="tokenModalVisible"
      title="Generate Invite Token"
      width="700px"
      @ok="confirmGenerateToken"
      @cancel="tokenModalVisible = false"
    >
      <!-- Usage Instructions -->
      <div style="background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px; padding: 16px; margin-bottom: 20px;">
        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
          <InfoCircleOutlined style="color: #52c41a; font-size: 16px;" />
          <span style="font-weight: 600; color: #52c41a; font-size: 14px;">How to Use Invite Tokens</span>
        </div>
        <div style="font-size: 13px; line-height: 1.6; color: #666;">
          <div style="margin-bottom: 8px;">
            <strong>Step 1:</strong> Fill out the form below to generate a unique invite token
          </div>
          <div style="margin-bottom: 8px;">
            <strong>Step 2:</strong> Share the generated token with the organization or individual
          </div>
          <div style="margin-bottom: 8px;">
            <strong>Step 3:</strong> They can use this token during registration to join your care team
          </div>
          <div style="margin-bottom: 8px;">
            <strong>Step 4:</strong> The token will automatically expire after the specified number of days
          </div>
          <div style="color: #ff4d4f; font-weight: 500;">
            <strong>Important:</strong> 
            <span v-if="tokenForm.tokenType === 'manager'">Manager tokens allow organizations to register new manager accounts</span>
            <span v-else-if="tokenForm.tokenType === 'worker'">Worker tokens allow organizations to register new worker accounts</span>
            <span v-else>Keep tokens secure and only share with trusted parties</span>
          </div>
        </div>
      </div>

      <a-form :model="tokenForm" layout="vertical">
        <a-form-item label="Worker Name" required>
          <a-input v-model:value="tokenForm.organizationName" placeholder="Enter worker name" />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            The name of the worker who will receive this token
          </div>
        </a-form-item>
        
        <a-form-item label="Token Type" required>
          <a-select v-model:value="tokenForm.tokenType" placeholder="Select token type" style="width: 100%;">
            <a-select-option value="manager">Manager</a-select-option>
            <a-select-option value="worker">Worker</a-select-option>
          </a-select>
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            <span v-if="tokenForm.tokenType === 'manager'">Manager tokens allow organizations to register new manager accounts</span>
            <span v-else-if="tokenForm.tokenType === 'worker'">Worker tokens allow organizations to register new worker accounts</span>
            <span v-else>Select the type of account this token will create</span>
          </div>
        </a-form-item>
        
        <a-form-item label="Expiration Days" required>
          <a-input-number 
            v-model:value="tokenForm.expirationDays" 
            :min="1" 
            :max="30" 
            style="width: 100%;"
            placeholder="Enter expiration days (1-30)"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            Token will be valid for this many days after generation
          </div>
        </a-form-item>
        
        <a-form-item label="Notes">
          <a-textarea 
            v-model:value="tokenForm.notes" 
            placeholder="Enter any additional notes for the organization"
            :rows="3"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            Optional notes that will be included with the token (e.g., contact information, special instructions)
          </div>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Generated Token Display Modal -->
    <a-modal
      v-model:open="tokenDisplayModalVisible"
      title="Existing Valid Token"
      width="600px"
      :footer="null"
    >
      <div style="text-align: center; padding: 20px;">
        <div style="margin-bottom: 20px;">
          <a-typography-title :level="4" style="color: #52c41a;">
            Valid Token Found!
          </a-typography-title>
          <p style="color: #666; margin-top: 8px;">
            You already have a valid invite token for {{ tokenForm.organizationName || 'this worker' }}.
          </p>
        </div>
        
        <div style="background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px; padding: 16px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 8px;">Invite Token:</div>
          <div style="font-family: monospace; font-size: 16px; color: #1890ff; word-break: break-all;">
            {{ generatedToken }}
          </div>
        </div>
        
        <div style="margin-bottom: 20px;">
          <a-button type="primary" @click="copyToken" style="margin-right: 8px;">
            Copy Token
          </a-button>
          <a-button @click="downloadToken" style="margin-right: 8px;">
            Download as Text
          </a-button>
          <a-button type="default" @click="forceGenerateNewToken">
            <template #icon><ReloadOutlined /></template>
            Generate New Token
          </a-button>
        </div>
        
        <!-- Detailed Usage Instructions -->
        <div style="text-align: left; background: #fff7e6; border: 1px solid #ffd591; border-radius: 6px; padding: 16px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 12px; color: #d46b08;">
            <InfoCircleOutlined style="margin-right: 6px;" />
            Instructions for {{ tokenForm.organizationName || 'the Worker' }}:
          </div>
          <div style="font-size: 13px; line-height: 1.6; color: #666;">
            <div style="margin-bottom: 8px;">
              <strong>1. Registration Process:</strong><br/>
              • Go to the registration page<br/>
              • Enter the invite token when prompted<br/>
              • Complete the registration form with your details
            </div>
            <div style="margin-bottom: 8px;">
              <strong>2. Token Details:</strong><br/>
              • Token Type: {{ tokenForm.tokenType === 'manager' ? 'Manager' : 'Worker' }}<br/>
              • Expires in: {{ tokenForm.expirationDays }} days<br/>
              • Generated: {{ new Date().toLocaleDateString() }}
            </div>
            <div style="margin-bottom: 8px;">
              <strong>3. After Registration:</strong><br/>
              <span v-if="tokenForm.tokenType === 'manager'">
                • You'll be automatically added to the care team as a Manager<br/>
                • You can start managing schedules and tasks<br/>
                • You can generate invite tokens for Workers<br/>
                • Contact the POA if you have any questions
              </span>
              <span v-else-if="tokenForm.tokenType === 'worker'">
                • You'll be automatically added to the care team as a Worker<br/>
                • You can view your assigned schedules and tasks<br/>
                • You can update task status and add notes<br/>
                • Contact your Manager if you have any questions
              </span>
              <span v-else>
                • You'll be automatically added to the care team<br/>
                • You can start managing schedules and tasks<br/>
                • Contact the team lead if you have any questions
              </span>
            </div>
            <div style="color: #ff4d4f; font-weight: 500;">
              <strong>⚠️ Important:</strong> 
              <span v-if="tokenForm.tokenType === 'manager'">This Manager token can be used multiple times within the expiration period and will expire automatically</span>
              <span v-else-if="tokenForm.tokenType === 'worker'">This Worker token can be used multiple times within the expiration period and will expire automatically</span>
              <span v-else>This token can only be used once and will expire automatically</span>
            </div>
          </div>
        </div>
        
        <!-- Contact Information -->
        <div v-if="tokenForm.notes" style="text-align: left; background: #f0f9ff; border: 1px solid #91d5ff; border-radius: 6px; padding: 12px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 8px; color: #1890ff;">Additional Notes:</div>
          <div style="font-size: 13px; line-height: 1.6; color: #666;">{{ tokenForm.notes }}</div>
        </div>
      </div>
    </a-modal>

    <!-- Daily Management Modal -->
    <a-modal
      v-model:open="dailyManagementModalVisible"
      title="Daily Management"
      width="1200px"
      @ok="confirmDailyManagement"
      @cancel="dailyManagementModalVisible = false"
    >
      <a-form :model="scheduleForm" layout="vertical">
        <a-form-item label="Select Date">
          <a-date-picker
            v-model:value="scheduleForm.date"
            style="width: 200px;"
            placeholder="Select date for schedule"
          />
        </a-form-item>
        
        <!-- Shift Information -->
        <div style="margin-bottom: 20px;">
          <h3 style="margin-bottom: 16px; color: #1890ff;">
            <ClockCircleOutlined style="margin-right: 8px;" />
            Shift Information
          </h3>
          
          <!-- Morning Shift -->
          <div style="margin-bottom: 20px; border: 1px solid #d9d9d9; border-radius: 8px; padding: 16px;">
            <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
              <ClockCircleOutlined style="color: #52c41a;" />
              <span style="font-weight: 500; color: #52c41a;">Morning Shift (8:00-16:00)</span>
            </div>
            <a-form-item label="Select morning shift workers">
              <a-select
                v-model:value="scheduleForm.morningShift"
                mode="multiple"
                placeholder="Click to select workers"
                style="width: 100%;"
                @change="onMorningShiftChange"
                :options="morningShiftOptions"
              >
                <template #notFoundContent>
                  <div style="text-align: center; color: #999; padding: 20px;">
                    No workers available
                  </div>
                </template>
                <template #option="{ value, label }">
                  <div style="display: flex; align-items: center; gap: 8px;">
                    <a-avatar 
                      :size="20" 
                      :src="getWorkerById(value)?.photo || getWorkerById(value)?.photoUrl"
                      @error="(e) => { const w = getWorkerById(value); if (w) { w.photo = null; w.photoUrl = null; } }"
                    >
                      {{ getWorkerById(value)?.name?.charAt(0)?.toUpperCase() }}
                    </a-avatar>
                    <span>{{ label }}</span>
                  </div>
                </template>
              </a-select>
            </a-form-item>
          </div>
          
          <!-- Afternoon Shift -->
          <div style="margin-bottom: 20px; border: 1px solid #d9d9d9; border-radius: 8px; padding: 16px;">
            <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
              <ClockCircleOutlined style="color: #1890ff;" />
              <span style="font-weight: 500; color: #1890ff;">Afternoon Shift (16:00-24:00)</span>
            </div>
            <a-form-item label="Select afternoon shift workers">
              <a-select
                v-model:value="scheduleForm.eveningShift"
                mode="multiple"
                placeholder="Click to select workers"
                style="width: 100%;"
                :options="afternoonShiftOptions"
              >
                <template #notFoundContent>
                  <div style="text-align: center; color: #999; padding: 20px;">
                    No workers available
                  </div>
                </template>
                <template #option="{ value, label }">
                  <div style="display: flex; align-items: center; gap: 8px;">
                    <a-avatar 
                      :size="20" 
                      :src="getWorkerById(value)?.photo || getWorkerById(value)?.photoUrl"
                      @error="(e) => { const w = getWorkerById(value); if (w) { w.photo = null; w.photoUrl = null; } }"
                    >
                      {{ getWorkerById(value)?.name?.charAt(0)?.toUpperCase() }}
                    </a-avatar>
                    <span>{{ label }}</span>
                  </div>
                </template>
              </a-select>
            </a-form-item>
          </div>
          
          <!-- Night Shift -->
          <div style="margin-bottom: 20px; border: 1px solid #d9d9d9; border-radius: 8px; padding: 16px;">
            <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
              <ClockCircleOutlined style="color: #722ed1;" />
              <span style="font-weight: 500; color: #722ed1;">Evening Shift (24:00-8:00)</span>
            </div>
            <a-form-item label="Select night shift workers">
              <a-select
                v-model:value="scheduleForm.nightShift"
                mode="multiple"
                placeholder="Click to select workers"
                style="width: 100%;"
                :options="nightShiftOptions"
              >
                <template #notFoundContent>
                  <div style="text-align: center; color: #999; padding: 20px;">
                    No workers available
                  </div>
                </template>
                <template #option="{ value, label }">
                  <div style="display: flex; align-items: center; gap: 8px;">
                    <a-avatar 
                      :size="20" 
                      :src="getWorkerById(value)?.photo || getWorkerById(value)?.photoUrl"
                      @error="(e) => { const w = getWorkerById(value); if (w) { w.photo = null; w.photoUrl = null; } }"
                    >
                      {{ getWorkerById(value)?.name?.charAt(0)?.toUpperCase() }}
                    </a-avatar>
                    <span>{{ label }}</span>
                  </div>
                </template>
              </a-select>
            </a-form-item>
          </div>
        </div>
        
        <!-- Photo Upload Section -->
        <div style="margin-top: 20px;">
          <h3 style="margin-bottom: 16px; color: #1890ff;">
            <UploadOutlined style="margin-right: 8px;" />
            Upload Photos for Workers
          </h3>
          <a-form-item label="Upload Photo for Selected Worker">
              <div v-if="selectedWorkerForPhoto" style="margin-bottom: 16px; padding: 12px; background: #f6ffed; border-radius: 6px;">
                <div style="display: flex; align-items: center; gap: 12px;">
                  <a-avatar 
                    :size="40" 
                    :src="selectedWorkerForPhoto.photo || selectedWorkerForPhoto.photoUrl"
                    @error="() => { selectedWorkerForPhoto.photo = null; selectedWorkerForPhoto.photoUrl = null; }"
                  >
                    {{ selectedWorkerForPhoto.name.charAt(0).toUpperCase() }}
                  </a-avatar>
                  <div>
                    <div style="font-weight: bold;">{{ selectedWorkerForPhoto.name }}</div>
                    <div style="font-size: 12px; color: #666;">{{ selectedWorkerForPhoto.workerId }} - {{ selectedWorkerForPhoto.position }}</div>
                  </div>
                </div>
              </div>
              
              <a-upload-dragger
                v-model:file-list="fileList"
                name="file"
                :before-upload="beforeUpload"
                @change="handleUploadChange"
                :disabled="!selectedWorkerForPhoto"
                list-type="picture-card"
                :show-upload-list="true"
                :max-count="1"
              >
                <div v-if="fileList.length === 0">
                  <p class="ant-upload-drag-icon">
                    <InboxOutlined />
                  </p>
                  <p class="ant-upload-text">
                    {{ selectedWorkerForPhoto ? `Upload photo for ${selectedWorkerForPhoto.name}` : 'Select a worker first' }}
                  </p>
                  <p class="ant-upload-hint">
                    {{ selectedWorkerForPhoto ? 'Click or drag photo to upload for this worker' : 'Please select a worker from the left to upload their photo' }}
                  </p>
                </div>
              </a-upload-dragger>
              
              <!-- Photo Preview -->
              <div v-if="fileList.length > 0" style="margin-top: 16px;">
                <h4 style="margin-bottom: 8px;">Photo Preview:</h4>
                <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                  <div
                    v-for="file in fileList"
                    :key="file.uid"
                    style="position: relative; border: 1px solid #d9d9d9; border-radius: 6px; padding: 8px;"
                  >
                    <img
                      :src="file.thumbUrl || file.url"
                      :alt="file.name"
                      style="width: 120px; height: 120px; object-fit: cover; border-radius: 4px;"
                    />
                    <div style="margin-top: 4px; font-size: 12px; text-align: center; color: #666;">
                      {{ file.name }}
                    </div>
                    <a-button
                      size="small"
                      danger
                      style="position: absolute; top: 4px; right: 4px;"
                      @click="removeFile(file.uid)"
                    >
                      ×
                    </a-button>
                  </div>
                </div>
              </div>
              
              <div style="margin-top: 16px;">
                <a-alert
                  message="Upload Guidelines"
                  description="Please ensure photos are clear and include worker's face and ID badge. Supported formats: JPG, PNG. Max file size: 5MB per photo."
                  type="info"
                  show-icon
                />
              </div>
            </a-form-item>
        </div>

        <a-form-item label="Schedule Notes">
          <a-textarea
            v-model:value="scheduleForm.notes"
            placeholder="Add any notes for this schedule"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>


    <!-- Worker Detail Modal -->
    <a-modal
      v-model:open="workerDetailModalVisible"
      title="Worker Details"
      width="600px"
      :footer="null"
      @cancel="workerDetailModalVisible = false"
    >
      <div v-if="selectedWorkerDetail" style="padding: 20px;">
        <!-- Worker Photo and Basic Info -->
        <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 24px; padding: 16px; background: #f6ffed; border-radius: 8px;">
          <a-avatar
ma a            :size="80"
            :src="selectedWorkerDetail.photo"
            :alt="selectedWorkerDetail.name"
            style="border: 3px solid #52c41a;"
          >
            {{ selectedWorkerDetail.name.charAt(0).toUpperCase() }}
          </a-avatar>
          <div style="flex: 1;">
            <h2 style="margin: 0 0 8px 0; color: #1890ff;">{{ selectedWorkerDetail.name }}</h2>
            <div style="font-size: 16px; color: #666; margin-bottom: 4px;">
              <strong>ID:</strong> {{ selectedWorkerDetail.workerId }}
            </div>
            <div style="font-size: 16px; color: #666; margin-bottom: 4px;">
              <strong>Position:</strong> {{ selectedWorkerDetail.position }}
            </div>
            <div style="font-size: 16px; color: #666;">
              <strong>Status:</strong> 
              <a-tag :color="getStatusColor(selectedWorkerDetail.status)">
                {{ selectedWorkerDetail.status }}
              </a-tag>
            </div>
          </div>
        </div>

        <!-- Detailed Information -->
        <a-row :gutter="[16, 16]">
          <a-col :span="12">
            <a-card size="small" title="Contact Information">
              <div style="margin-bottom: 8px;">
                <strong>Email:</strong> {{ selectedWorkerDetail.email }}
              </div>
              <div>
                <strong>Join Date:</strong> {{ selectedWorkerDetail.joinDate }}
              </div>
            </a-card>
          </a-col>
          
          <a-col :span="12">
            <a-card size="small" title="Work Schedule">
              <div style="margin-bottom: 8px;">
                <strong>Today's Status:</strong> 
                <a-tag color="green">Scheduled</a-tag>
              </div>
              <div style="margin-bottom: 8px;">
                <strong>Shift:</strong> Morning (8:00-16:00)
              </div>
              <div>
                <strong>Last Photo:</strong> 
                <a-tag color="blue">Uploaded Today</a-tag>
              </div>
            </a-card>
          </a-col>
        </a-row>

        <!-- Photo Gallery -->
        <div style="margin-top: 20px;">
          <h3 style="margin-bottom: 12px;">Recent Photos</h3>
          <div style="display: flex; gap: 12px; flex-wrap: wrap;">
            <div
              v-if="selectedWorkerDetail.photo || selectedWorkerDetail.photoUrl"
              style="position: relative; border: 1px solid #d9d9d9; border-radius: 6px; padding: 8px;"
            >
                <img
                :src="selectedWorkerDetail.photo || selectedWorkerDetail.photoUrl"
                :alt="selectedWorkerDetail.name"
                style="width: 100px; height: 100px; object-fit: cover; border-radius: 4px;"
              />
              <div style="margin-top: 4px; font-size: 12px; text-align: center; color: #666;">
                Today
              </div>
            </div>
            <div style="border: 2px dashed #d9d9d9; border-radius: 6px; padding: 8px; width: 100px; height: 100px; display: flex; align-items: center; justify-content: center; color: #999;">
              <div style="text-align: center;">
                <div style="font-size: 24px; margin-bottom: 4px;">📷</div>
                <div style="font-size: 10px;">No more photos</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div style="margin-top: 24px;">
          <h4 style="margin-bottom: 12px; color: #1890ff;">Worker Management Actions</h4>
          <div style="display: flex; gap: 12px; justify-content: center; flex-wrap: wrap;">
            <a-button type="primary" @click="editWorkerStatusFromDetail">
              Edit Status
            </a-button>
            <a-button @click="uploadPhotoFromDetail">
              Upload Photo
            </a-button>
            <a-button danger @click="removeWorkerFromTeam">
              Remove from Team
            </a-button>
          </div>
          <div style="margin-top: 12px; padding: 12px; background: #f6ffed; border-radius: 6px; font-size: 12px; color: #666;">
            <div><strong>Edit Status:</strong> Change worker status (Active/On leave) - keeps historical data</div>
            <div><strong>Remove from Team:</strong> Remove worker from current patient team but keep in organization</div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- Edit Worker Status Modal -->
    <a-modal
      v-model:open="editStatusModalVisible"
      title="Edit Worker Status"
      width="500px"
      @ok="confirmEditStatus"
      @cancel="editStatusModalVisible = false"
    >
      <div v-if="selectedWorkerDetail" style="margin-bottom: 20px;">
        <div style="display: flex; align-items: center; gap: 12px; padding: 12px; background: #f6ffed; border-radius: 6px;">
          <a-avatar 
            :size="40" 
            :src="selectedWorkerDetail.photo || selectedWorkerDetail.photoUrl"
            @error="() => { selectedWorkerDetail.photo = null; selectedWorkerDetail.photoUrl = null; }"
          >
            {{ selectedWorkerDetail.name.charAt(0).toUpperCase() }}
          </a-avatar>
          <div>
            <div style="font-weight: bold;">{{ selectedWorkerDetail.name }}</div>
            <div style="font-size: 12px; color: #666;">{{ selectedWorkerDetail.workerId }} - {{ selectedWorkerDetail.position }}</div>
          </div>
        </div>
      </div>

      <a-form :model="editStatusForm" layout="vertical">
        <a-form-item label="Current Status">
          <a-tag :color="getStatusColor(selectedWorkerDetail?.status)">
            {{ selectedWorkerDetail?.status }}
          </a-tag>
        </a-form-item>

        <a-form-item label="New Status" required>
          <a-select
            v-model:value="editStatusForm.newStatus"
            placeholder="Select new status"
            style="width: 100%;"
          >
            <a-select-option value="Active">
              <a-tag color="green">Active</a-tag>
            </a-select-option>
            <a-select-option value="On leave">
              <a-tag color="orange">On leave</a-tag>
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="Reason for Status Change">
          <a-textarea
            v-model:value="editStatusForm.reason"
            placeholder="Explain the reason for status change (optional)"
            :rows="3"
          />
        </a-form-item>

      </a-form>
    </a-modal>

    <!-- Remove Worker Confirmation Modal -->
    <a-modal
      v-model:open="removeConfirmationModalVisible"
      title="Remove Worker"
      width="500px"
      @ok="confirmRemoveWorker"
      @cancel="removeConfirmationModalVisible = false"
    >
      <div style="text-align: center; padding: 20px;">
        <div style="margin-bottom: 20px;">
          <a-typography-title :level="4" style="color: #ff4d4f;">
            Confirm Worker Removal
          </a-typography-title>
        </div>
        
        <div v-if="workerToRemove" style="background: #fff2f0; border: 1px solid #ffccc7; border-radius: 6px; padding: 16px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 8px;">Worker Information:</div>
          <div><strong>Name:</strong> {{ workerToRemove.name }}</div>
          <div><strong>ID:</strong> {{ workerToRemove.workerId }}</div>
          <div><strong>Position:</strong> {{ workerToRemove.position }}</div>
          <div><strong>Status:</strong> {{ workerToRemove.status }}</div>
        </div>
        
        <a-alert
          message="Warning: This action cannot be undone"
          description="Removing this worker will permanently delete their record from the system. All associated data, schedules, and assignments will be lost."
          type="error"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <div style="text-align: left; background: #fff7e6; border: 1px solid #ffd591; border-radius: 6px; padding: 12px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 8px;">What will be removed:</div>
          <div style="font-size: 12px; line-height: 1.5;">
            • Worker profile and personal information<br/>
            • All assigned tasks and schedules<br/>
            • Historical work records<br/>
            • Access permissions and credentials
          </div>
        </div>
        
        <div style="margin-bottom: 20px;">
          <a-checkbox v-model:checked="removeConfirmationChecked">
            I understand this action is permanent and cannot be undone
          </a-checkbox>
        </div>
      </div>
    </a-modal>

    <!-- Remove Worker from Team Modal -->
    <a-modal
      v-model:open="removeFromTeamModalVisible"
      title="Remove Worker from Team"
      width="500px"
      @ok="confirmRemoveFromTeam"
      @cancel="removeFromTeamModalVisible = false"
    >
      <div v-if="selectedWorkerDetail" style="margin-bottom: 20px;">
        <div style="display: flex; align-items: center; gap: 12px; padding: 12px; background: #fff2e8; border-radius: 6px;">
          <a-avatar 
            :size="40" 
            :src="selectedWorkerDetail.photo || selectedWorkerDetail.photoUrl"
            @error="() => { selectedWorkerDetail.photo = null; selectedWorkerDetail.photoUrl = null; }"
          >
            {{ selectedWorkerDetail.name.charAt(0).toUpperCase() }}
          </a-avatar>
          <div>
            <div style="font-weight: bold;">{{ selectedWorkerDetail.name }}</div>
            <div style="font-size: 12px; color: #666;">{{ selectedWorkerDetail.workerId }} - {{ selectedWorkerDetail.position }}</div>
          </div>
        </div>
      </div>

      <a-form :model="removeFromTeamForm" layout="vertical">
        <a-form-item label="Reason for Removal">
          <a-select
            v-model:value="removeFromTeamForm.reason"
            placeholder="Select reason"
            style="width: 100%;"
          >
            <a-select-option value="Service completed">Service completed</a-select-option>
            <a-select-option value="Patient request">Patient request</a-select-option>
            <a-select-option value="Schedule conflict">Schedule conflict</a-select-option>
            <a-select-option value="Performance issue">Performance issue</a-select-option>
            <a-select-option value="Other">Other</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="Additional Notes">
          <a-textarea
            v-model:value="removeFromTeamForm.notes"
            placeholder="Add any additional notes (optional)"
            :rows="3"
          />
        </a-form-item>

        <a-form-item label="Effective Date">
          <a-date-picker
            v-model:value="removeFromTeamForm.effectiveDate"
            style="width: 100%;"
            placeholder="Select effective date"
          />
        </a-form-item>

        <a-alert
          message="Important Notice"
          description="This action will remove the worker from the current patient team but keep them in the organization. They can be reassigned to other patients or teams."
          type="info"
          show-icon
          style="margin-bottom: 16px;"
        />
      </a-form>
    </a-modal>

    <!-- Shift Time Settings Modal -->
    <a-modal
      v-model:open="shiftTimeSettingsModalVisible"
      title="Shift Time Settings"
      width="600px"
      @ok="confirmShiftTimeSettings"
      @cancel="shiftTimeSettingsModalVisible = false"
    >
      <div style="margin-bottom: 20px;">
        <a-alert
          message="Configure Default Shift Times"
          description="Set the default start and end times for each shift. These settings will be used as defaults for all future schedules and will be saved locally."
          type="info"
          show-icon
          style="margin-bottom: 16px;"
        />
      </div>

      <a-form :model="shiftTimeForm" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="Morning Shift">
              <div style="margin-bottom: 8px;">
                <label style="font-size: 12px; color: #666;">Start Time:</label>
                <a-time-picker
                  v-model:value="shiftTimeForm.morningStart"
                  format="HH:mm"
                  style="width: 100%;"
                  placeholder="Start time"
                />
              </div>
              <div>
                <label style="font-size: 12px; color: #666;">End Time:</label>
                <a-time-picker
                  v-model:value="shiftTimeForm.morningEnd"
                  format="HH:mm"
                  style="width: 100%;"
                  placeholder="End time"
                />
              </div>
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item label="Afternoon Shift">
              <div style="margin-bottom: 8px;">
                <label style="font-size: 12px; color: #666;">Start Time:</label>
                <a-time-picker
                  v-model:value="shiftTimeForm.afternoonStart"
                  format="HH:mm"
                  style="width: 100%;"
                  placeholder="Start time"
                />
              </div>
              <div>
                <label style="font-size: 12px; color: #666;">End Time:</label>
                <a-time-picker
                  v-model:value="shiftTimeForm.afternoonEnd"
                  format="HH:mm"
                  style="width: 100%;"
                  placeholder="End time"
                />
              </div>
            </a-form-item>
          </a-col>
          
          <a-col :span="8">
            <a-form-item label="Evening Shift">
              <div style="margin-bottom: 8px;">
                <label style="font-size: 12px; color: #666;">Start Time:</label>
                <a-time-picker
                  v-model:value="shiftTimeForm.nightStart"
                  format="HH:mm"
                  style="width: 100%;"
                  placeholder="Start time"
                />
              </div>
              <div>
                <label style="font-size: 12px; color: #666;">End Time:</label>
                <a-time-picker
                  v-model:value="shiftTimeForm.nightEnd"
                  format="HH:mm"
                  style="width: 100%;"
                  placeholder="End time"
                />
              </div>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

  </div>
</template>

<script setup>
import { ref, onMounted, h, resolveComponent, computed } from 'vue'
import dayjs from 'dayjs'
import { 
  QuestionCircleOutlined, 
  UserAddOutlined, 
  UploadOutlined, 
  InboxOutlined,
  UserOutlined,
  TeamOutlined,
  CalendarOutlined,
  InfoCircleOutlined,
  ReloadOutlined,
  ClockCircleOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { addRemovedWorker } from '@/services/userService'
import { 
  getAllWorkers,
  getWorkersByOrganization, 
  getWorkersByManagerId,
  createWorker, 
  updateWorker, 
  deleteWorker, 
  activateWorker, 
  deactivateWorker,
  getAvailableWorkers,
  createDailySchedule,
  getDailySchedule,
  clearDailySchedule,
  uploadWorkerPhoto as uploadWorkerPhotoAPI
} from '@/services/workerService'
import { getMe } from '@/services/userService'
import { generateInviteCode } from '@/services/inviteCodeService'
import { getBaseUrl } from '@/services/api'

// Reactive data
const loading = ref(false)
const generatingToken = ref(false)
const generatedToken = ref(null)
const tokenModalVisible = ref(false)
const tokenDisplayModalVisible = ref(false)
const uploadModalVisible = ref(false)
const dailyManagementModalVisible = ref(false)
const fileList = ref([])
const selectedWorker = ref(null)
const selectedDate = ref(null)
const activeTab = ref('schedule')
const selectedWorkerForPhoto = ref(null)
const workerDetailModalVisible = ref(false)
const selectedWorkerDetail = ref(null)
const editStatusModalVisible = ref(false)
const removeFromTeamModalVisible = ref(false)
const removeConfirmationModalVisible = ref(false)
const workerToRemove = ref(null)
const removeConfirmationChecked = ref(false)
const selectedShift = ref('morning')
const shiftTimeSettingsModalVisible = ref(false)

// Computed properties
const morningShiftOptions = computed(() => {
  return availableWorkers.value.map(worker => ({
    value: worker.id,
    label: `${worker.name} (${worker.workerId})`
  }))
})

const afternoonShiftOptions = computed(() => {
  return availableWorkers.value.map(worker => ({
    value: worker.id,
    label: `${worker.name} (${worker.workerId})`
  }))
})

const nightShiftOptions = computed(() => {
  return availableWorkers.value.map(worker => ({
    value: worker.id,
    label: `${worker.name} (${worker.workerId})`
  }))
})

// Forms
const tokenForm = ref({
  organizationName: '',
  tokenType: 'worker',
  expirationDays: 7,
  notes: ''
})


const editStatusForm = ref({
  newStatus: '',
  reason: ''
})

const removeFromTeamForm = ref({
  reason: '',
  notes: '',
  effectiveDate: null
})

const scheduleForm = ref({
  date: null,
  morningShift: [],
  eveningShift: [],
  nightShift: [],
  notes: ''
})

const photoForm = ref({
  date: null
})

const shiftTimeForm = ref({
  morningStart: null,
  morningEnd: null,
  afternoonStart: null,
  afternoonEnd: null,
  nightStart: null,
  nightEnd: null
})

const shiftTimeSettings = ref({
  morning: { start: '06:00', end: '14:00' },
  afternoon: { start: '14:00', end: '22:00' },
  night: { start: '22:00', end: '06:00' }
})

// Worker data from API
const workers = ref([])
const currentUser = ref(null)
const organizationId = ref(null)

// Daily workers (subset of active workers)
const dailyWorkers = ref([])

// Available workers (all active workers)
const availableWorkers = ref([])

// Daily schedules data
const dailySchedules = ref({
  // Mock data for different dates
  '2024-01-15': [1, 2, 4, 5, 6], // John, Sarah, Emily, David, Lisa
  '2024-01-16': [1, 3, 4, 6, 7], // John, Michael, Emily, Robert, Jennifer
  '2024-01-17': [2, 4, 5, 7, 8], // Sarah, Emily, David, Jennifer, (假设有第8个员工)
  '2024-01-18': [1, 2, 5, 6, 7], // John, Sarah, David, Lisa, Robert
  '2024-01-19': [3, 4, 6, 7, 8]  // Michael, Emily, Lisa, Robert, Jennifer
})

// Table columns
const workerColumns = [
  {
    title: 'Photo',
    key: 'photo',
    width: 80,
    align: 'center'
  },
  {
    title: 'Worker ID',
    dataIndex: 'workerId',
    key: 'workerId',
    width: 100
  },
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    width: 150
  },
  {
    title: 'Status',
    key: 'status',
    width: 100,
    align: 'center'
  },
  {
    title: 'Email',
    dataIndex: 'email',
    key: 'email',
    width: 200
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 120,
    align: 'center'
  }
]

// Methods
const generateInviteToken = async () => {
  // Determine the token type based on current user role
  // POA generates manager tokens, Manager generates worker tokens
  const defaultTokenType = currentUser.value?.role === 'poa' ? 'manager' : 'worker'
  
  // Check if there's already a valid token created by current user for this target type
  try {
    if (!currentUser.value?.id) {
      throw new Error('User not authenticated')
    }
    
    // Get all invite codes created by current user
    const { getMyInviteCodes } = await import('@/services/inviteCodeService')
    const myCodesResponse = await getMyInviteCodes(currentUser.value.id)
    
    if (myCodesResponse?.data && Array.isArray(myCodesResponse.data)) {
      const targetType = defaultTokenType.toUpperCase() // 'manager' -> 'MANAGER', 'worker' -> 'WORKER'
      const now = new Date()
      
      // Find valid token matching the target type
      const validToken = myCodesResponse.data.find(code => {
        if (!code.targetType || code.targetType !== targetType) {
          return false // Not matching target type
        }
        
        // Check if token is not expired
        if (code.expiresAt) {
          const expirationDate = new Date(code.expiresAt)
          return expirationDate > now
        }
        
        return false
      })
      
      if (validToken) {
        // Found valid token for this target type, show it
        console.log('✅ Found existing valid token for', targetType, ':', validToken.code)
        generatedToken.value = validToken.code
        tokenForm.value.tokenType = defaultTokenType
        tokenForm.value.organizationName = 'Worker' // Default, user can change this later
        
        // Show token display modal directly
        tokenDisplayModalVisible.value = true
        return
      } else {
        console.log('⚠️ No valid token found for', targetType, '- showing form to generate new one')
      }
    }
  } catch (error) {
    console.log('No existing token found or error checking:', error)
  }
  
  // Clear previous token and set default token type
  generatedToken.value = null
  tokenForm.value.tokenType = defaultTokenType
  tokenDisplayModalVisible.value = false
  tokenModalVisible.value = true
}

const confirmGenerateToken = async () => {
  if (!tokenForm.value.organizationName) {
    message.error('Please fill in worker name')
    return
  }

  generatingToken.value = true
  try {
    message.loading('Generating invite token...', 0)
    
    if (!currentUser.value) {
      throw new Error('User not authenticated')
    }
    
    // Prepare invite data
    const inviteData = {
      createdBy: currentUser.value.id,
      createdByType: currentUser.value.role === 'manager' ? 'MANAGER' : 'POA',
      targetType: tokenForm.value.tokenType.toUpperCase(), // Convert 'manager' to 'MANAGER', 'worker' to 'WORKER'
      patientId: currentUser.value.patientId || 'default-patient',
      organizationId: organizationId.value || 'default-org'
    }
    
    // Generate invite code via API
    const response = await generateInviteCode(inviteData)
    
    if (response.data) {
      generatedToken.value = response.data
      
      // Calculate token expiration (7 days from now)
      const expirationDate = new Date()
      expirationDate.setDate(expirationDate.getDate() + tokenForm.value.expirationDays)
      
      // Store token information in backend Patient record
      try {
        const { getPatientById, updatePatient, createPatient } = await import('@/services/patientService')
        
        // Prepare patient data with token information
        const patientData = {
          inviteToken: response.data,
          organizationName: tokenForm.value.organizationName,
          organizationId: currentUser.value.organizationId || 'default-org',
          tokenExpiration: expirationDate.toISOString(),
          createdBy: currentUser.value.id || 'manager',
          createdAt: new Date().toISOString(),
          poaId: currentUser.value.id || 'manager'
        }
        
        console.log('📤 Storing token data to backend:', patientData)
        
        if (currentUser.value?.patientId) {
          // Update existing patient
          const updateResult = await updatePatient(currentUser.value.patientId, patientData)
          console.log('📥 Token stored in existing patient record:', updateResult)
        } else {
          // Create new patient with basic info
          const basicPatientData = {
            firstName: 'Client',
            lastName: 'Name',
            age: 0,
            medicalConditions: '',
            specialRequirements: '',
            ...patientData
          }
          const createResult = await createPatient(basicPatientData)
          console.log('📥 Token stored in new patient record:', createResult)
        }
        
        console.log('✅ Token information stored successfully in backend')
      } catch (patientError) {
        console.error('Failed to store token in backend:', patientError)
        // Don't fail the whole operation if backend storage fails
      }
      
      message.destroy()
      message.success('Invite token generated successfully!')
      
      // Close form modal and open token display modal
      tokenModalVisible.value = false
      tokenDisplayModalVisible.value = true
      
      // Reset form
      tokenForm.value = {
        organizationName: '',
        tokenType: 'worker',
        expirationDays: 7,
        notes: ''
      }
    }
  } catch (error) {
    message.destroy()
    console.error('Failed to generate invite token:', error)
    message.error(error.message || 'Failed to generate invite token')
  } finally {
    generatingToken.value = false
  }
}

const showUploadModal = () => {
  uploadModalVisible.value = true
}

// Token management functions
const copyToken = async () => {
  try {
    await navigator.clipboard.writeText(generatedToken.value)
    message.success('Token copied to clipboard!')
  } catch (error) {
    message.error('Failed to copy token')
  }
}

const forceGenerateNewToken = () => {
  // Close token display modal and open form modal
  tokenDisplayModalVisible.value = false
  tokenModalVisible.value = true
}

const downloadToken = () => {
  const tokenData = {
    token: generatedToken.value,
    organization: tokenForm.value.organizationName,
    tokenType: tokenForm.value.tokenType,
    expirationDays: tokenForm.value.expirationDays,
    generatedDate: new Date().toISOString(),
    notes: tokenForm.value.notes
  }
  
  const blob = new Blob([JSON.stringify(tokenData, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `worker-invite-token-${tokenForm.value.organizationName.replace(/\s+/g, '-')}.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  
  message.success('Token data downloaded successfully!')
}

const showDailyManagementModal = () => {
  dailyManagementModalVisible.value = true
  activeTab.value = 'schedule'
  // Set default date to today
  scheduleForm.value.date = selectedDate.value || dayjs()
  photoForm.value.date = selectedDate.value || dayjs()
  // Reset selected worker for photo
  selectedWorkerForPhoto.value = null
  fileList.value = []
}

const handleUploadWorkerPhoto = (worker) => {
  selectedWorkerForPhoto.value = worker
  fileList.value = [] // Clear previous files
  message.info(`Ready to upload photo for ${worker.name}`)
}

const onWorkerSelectForPhoto = (worker) => {
  selectedWorkerForPhoto.value = worker
  fileList.value = [] // Clear previous files
  message.info(`Ready to upload photo for ${worker.name}`)
}

const onMorningShiftChange = (checkedValues) => {
  // If any workers are selected for morning shift, set the first one as selected for photo upload
  if (checkedValues.length > 0) {
    const firstSelectedWorkerId = checkedValues[0]
    const selectedWorker = availableWorkers.value.find(w => w.id === firstSelectedWorkerId)
    if (selectedWorker) {
      selectedWorkerForPhoto.value = selectedWorker
      fileList.value = [] // Clear previous files
    }
  } else {
    // If no workers selected, clear the photo upload selection
    selectedWorkerForPhoto.value = null
    fileList.value = []
  }
}

const toggleMorningShiftWorker = (workerId) => {
  const currentIndex = scheduleForm.morningShift.indexOf(workerId)
  if (currentIndex > -1) {
    // Remove worker if already selected
    scheduleForm.morningShift.splice(currentIndex, 1)
  } else {
    // Add worker if not selected
    scheduleForm.morningShift.push(workerId)
  }
  
  // Trigger the change event to update photo upload selection
  onMorningShiftChange(scheduleForm.morningShift)
}

const toggleEveningShiftWorker = (workerId) => {
  const currentIndex = scheduleForm.eveningShift.indexOf(workerId)
  if (currentIndex > -1) {
    // Remove worker if already selected
    scheduleForm.eveningShift.splice(currentIndex, 1)
  } else {
    // Add worker if not selected
    scheduleForm.eveningShift.push(workerId)
  }
}

const toggleNightShiftWorker = (workerId) => {
  const currentIndex = scheduleForm.nightShift.indexOf(workerId)
  if (currentIndex > -1) {
    // Remove worker if already selected
    scheduleForm.nightShift.splice(currentIndex, 1)
  } else {
    // Add worker if not selected
    scheduleForm.nightShift.push(workerId)
  }
}

const getWorkerById = (workerId) => {
  return availableWorkers.value.find(worker => worker.id === workerId)
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('You can only upload image files!')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('Image must be smaller than 5MB!')
    return false
  }
  return false // Prevent auto upload
}

const handleUploadChange = (info) => {
  let fileList = [...info.fileList]
  
  // Limit to 1 file
  fileList = fileList.slice(-1)
  
  // Generate preview URL for images
  fileList = fileList.map(file => {
    if (file.originFileObj) {
      file.thumbUrl = URL.createObjectURL(file.originFileObj)
    }
    return file
  })
  
  // Update the file list
  fileList.value = fileList
}

const removeFile = (uid) => {
  fileList.value = fileList.value.filter(file => file.uid !== uid)
}

const confirmUploadPhotos = async () => {
  if (fileList.value.length === 0) {
    message.warning('Please select photos to upload')
    return
  }
  
  try {
    // Simulate upload process
    message.loading('Uploading photos...', 0)
    await new Promise(resolve => setTimeout(resolve, 2000))
    message.destroy()
    
    message.success(`Successfully uploaded ${fileList.value.length} photos`)
    uploadModalVisible.value = false
    fileList.value = []
  } catch (error) {
    message.destroy()
    message.error('Failed to upload photos')
  }
}


const getStatusColor = (status) => {
  switch (status) {
    case 'Active': return 'green'
    case 'On leave': return 'orange'
    case 'Inactive': return 'red'
    default: return 'default'
  }
}

const editWorker = (worker) => {
  selectedWorkerDetail.value = worker
  editStatusForm.value = {
    newStatus: worker.status || 'Active',
    reason: ''
  }
  editStatusModalVisible.value = true
}

const showRemoveConfirmation = (worker) => {
  workerToRemove.value = worker
  removeConfirmationChecked.value = false
  removeConfirmationModalVisible.value = true
}

const confirmRemoveWorker = async () => {
  if (!removeConfirmationChecked.value) {
    message.error('Please confirm that you understand this action is permanent')
    return
  }
  
  if (!workerToRemove.value) {
    message.error('No worker selected for removal')
    return
  }
  
  try {
    const workerId = workerToRemove.value.id
    
    // Call API to delete worker
    await deleteWorker(workerId)
    
    // Add worker to removed workers list (prevents future login)
    addRemovedWorker({
      email: workerToRemove.value.email,
      name: workerToRemove.value.name,
      workerId: workerToRemove.value.workerId
    })
    
    // Remove worker from local arrays
    const index = workers.value.findIndex(w => w.id === workerId)
    if (index > -1) {
      workers.value.splice(index, 1)
    }
    
    // Remove from available workers if present
    const availableIndex = availableWorkers.value.findIndex(w => w.id === workerId)
    if (availableIndex > -1) {
      availableWorkers.value.splice(availableIndex, 1)
    }
    
    // Remove from daily workers if present
    const dailyIndex = dailyWorkers.value.findIndex(w => w.id === workerId)
    if (dailyIndex > -1) {
      dailyWorkers.value.splice(dailyIndex, 1)
    }
    
    message.success(`Worker ${workerToRemove.value.name} has been removed successfully and can no longer access the system`)
    
    // Close modal and reset
    removeConfirmationModalVisible.value = false
    workerToRemove.value = null
    removeConfirmationChecked.value = false
    
  } catch (error) {
    console.error('Failed to remove worker:', error)
    message.error(error.message || 'Failed to remove worker')
  }
}


const filterOption = (input, option) => {
  return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

const selectWorker = (worker) => {
  selectedWorker.value = worker
  message.info(`Selected worker: ${worker.name} (${worker.workerId})`)
}

const showWorkerDetail = (worker) => {
  selectedWorkerDetail.value = worker
  workerDetailModalVisible.value = true
}

const editWorkerStatusFromDetail = () => {
  editStatusForm.value = {
    newStatus: selectedWorkerDetail.value.status || 'Active',
    reason: ''
  }
  editStatusModalVisible.value = true
}

const uploadPhotoFromDetail = () => {
  workerDetailModalVisible.value = false
  // Set the worker for photo upload
  selectedWorkerForPhoto.value = selectedWorkerDetail.value
  // Open daily management modal
  dailyManagementModalVisible.value = true
  message.info(`Ready to upload photo for ${selectedWorkerDetail.value.name}`)
}


const removeWorkerFromTeam = () => {
  removeFromTeamForm.value = {
    reason: '',
    notes: '',
    effectiveDate: dayjs()
  }
  removeFromTeamModalVisible.value = true
}

const confirmEditStatus = async () => {
  if (!editStatusForm.value.newStatus) {
    message.error('Please select a new status')
    return
  }

  try {
    message.loading('Updating worker status...', 0)
    
    const workerData = {
      id: selectedWorkerDetail.value.id,
      status: editStatusForm.value.newStatus,
      reason: editStatusForm.value.reason
    }
    
    const response = await updateWorker(workerData)
    
    if (response.data) {
      // Update worker status in the workers array
      const workerIndex = workers.value.findIndex(w => w.id === selectedWorkerDetail.value.id)
      if (workerIndex !== -1) {
        workers.value[workerIndex].status = editStatusForm.value.newStatus
      }

      // Update in dailyWorkers if present
      const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === selectedWorkerDetail.value.id)
      if (dailyWorkerIndex !== -1) {
        dailyWorkers.value[dailyWorkerIndex].status = editStatusForm.value.newStatus
      }

      // Update selectedWorkerDetail
      selectedWorkerDetail.value.status = editStatusForm.value.newStatus

      message.destroy()
      message.success(`Successfully updated ${selectedWorkerDetail.value.name}'s status to ${editStatusForm.value.newStatus}`)
      editStatusModalVisible.value = false
    } else {
      message.destroy()
      message.error('Failed to update worker status')
    }
  } catch (error) {
    message.destroy()
    console.error('Failed to update worker status:', error)
    message.error(error.message || 'Failed to update worker status')
  }
}

const confirmRemoveFromTeam = async () => {
  if (!removeFromTeamForm.value.reason) {
    message.error('Please select a reason for removal')
    return
  }

  try {
    message.loading('Removing worker from team...', 0)
    await new Promise(resolve => setTimeout(resolve, 1500))

    // Remove worker from dailyWorkers (current team)
    const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === selectedWorkerDetail.value.id)
    if (dailyWorkerIndex !== -1) {
      dailyWorkers.value.splice(dailyWorkerIndex, 1)
    }

    // Update daily schedules to remove this worker
    const today = selectedDate.value.format('YYYY-MM-DD')
    if (dailySchedules.value[today]) {
      dailySchedules.value[today] = dailySchedules.value[today].filter(id => id !== selectedWorkerDetail.value.id)
    }

    message.destroy()
    message.success(`Successfully removed ${selectedWorkerDetail.value.name} from the current patient team`)
    removeFromTeamModalVisible.value = false
    workerDetailModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to remove worker from team')
  }
}

const showScheduleModal = () => {
  scheduleModalVisible.value = true
  // Set default date to today
  scheduleForm.value.date = selectedDate.value || dayjs()
}

const confirmDailyManagement = async () => {
  // Handle schedule update
  if (!scheduleForm.value.date) {
    message.warning('Please select a date')
    return
  }
  
  // Check if at least one shift has workers assigned
  const hasWorkers = scheduleForm.value.morningShift.length > 0 || 
                    scheduleForm.value.eveningShift.length > 0 || 
                    scheduleForm.value.nightShift.length > 0
  
  if (!hasWorkers) {
    message.warning('Please select at least one worker for any shift')
    return
  }
  
  try {
    const dateStr = scheduleForm.value.date.format('YYYY-MM-DD')
    
    // Prepare schedule data for API
    // Frontend shifts: 
    //   - morningShift (8:00-16:00) → morningShiftWorkerIds
    //   - eveningShift (16:00-24:00, labeled as "Afternoon Shift") → afternoonShiftWorkerIds  
    //   - nightShift (24:00-8:00, labeled as "Evening Shift") → eveningShiftWorkerIds
    const scheduleData = {
      scheduleDate: dateStr,
      morningShiftWorkerIds: scheduleForm.value.morningShift || [],
      afternoonShiftWorkerIds: scheduleForm.value.eveningShift || [], // Frontend's "Afternoon Shift" (16:00-24:00)
      eveningShiftWorkerIds: scheduleForm.value.nightShift || [], // Frontend's "Night Shift" / "Evening Shift" (24:00-8:00)
      scheduleNotes: scheduleForm.value.notes || ''
    }
    
    // Get manager ID from current user
    if (!currentUser.value?.id) {
      message.error('User not authenticated. Please login again.')
      return
    }
    
    const managerId = currentUser.value.id
    
    // Call API to save schedule to backend
    message.loading('Saving schedule...', 0)
    try {
      await createDailySchedule(scheduleData, managerId)
      message.destroy()
      message.success('Schedule saved successfully!')
      
      // After successful save, reload the schedule for the selected date
      if (dateStr === selectedDate.value?.format('YYYY-MM-DD')) {
        await loadDailySchedule(dateStr)
      }
    } catch (apiError) {
      message.destroy()
      console.error('Failed to save schedule to backend:', apiError)
      message.error(`Failed to save schedule: ${apiError.message || 'Unknown error'}`)
      return // Don't continue if API call fails
    }
    
    // Combine all selected workers from all shifts for local state
    const allSelectedWorkers = [
      ...scheduleForm.value.morningShift,
      ...scheduleForm.value.eveningShift,
      ...scheduleForm.value.nightShift
    ]
    
    dailySchedules.value[dateStr] = allSelectedWorkers
    
    // Handle photo upload if there are files (do this BEFORE closing modal)
    if (fileList.value.length > 0 && selectedWorkerForPhoto.value) {
      try {
      message.loading(`Uploading photo for ${selectedWorkerForPhoto.value.name}...`, 0)
      
      const uploadedFile = fileList.value[0]
        const fileToUpload = uploadedFile.originFileObj || uploadedFile
        
        if (!fileToUpload) {
          throw new Error('No file selected for upload')
        }
        
        console.log('📸 Starting photo upload for worker:', selectedWorkerForPhoto.value.id, selectedWorkerForPhoto.value.name)
        console.log('📸 File to upload:', fileToUpload.name, fileToUpload.size, 'bytes')
        
        // Call API to upload photo
        const response = await uploadWorkerPhotoAPI(selectedWorkerForPhoto.value.id, fileToUpload)
        
        console.log('📸 Upload API response received:', response)
        
        if (response.data) {
          let photoUrl = response.data.photoUrl || response.data.photo
          console.log('📸 Upload response:', response.data)
          console.log('📸 Photo URL from response (raw):', photoUrl)
          
          // Convert relative path to full URL using API endpoint
          if (photoUrl && photoUrl.startsWith('/uploads/')) {
            const API_BASE_URL = getBaseUrl()
            // Use API endpoint for more reliable file serving
            photoUrl = `${API_BASE_URL}/api/files/serve?path=${encodeURIComponent(photoUrl)}`
            console.log('📸 Photo URL converted to:', photoUrl)
          }
          
        // Update the worker's photo in the workers array
        const workerIndex = workers.value.findIndex(w => w.id === selectedWorkerForPhoto.value.id)
        if (workerIndex !== -1) {
            workers.value[workerIndex].photo = photoUrl
            workers.value[workerIndex].photoUrl = photoUrl
            console.log('📸 Updated worker in memory:', workers.value[workerIndex])
          }
          
          // Update the worker's photo in the dailyWorkers array if they are currently displayed
          const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === selectedWorkerForPhoto.value.id)
          if (dailyWorkerIndex !== -1) {
            dailyWorkers.value[dailyWorkerIndex].photo = photoUrl
            dailyWorkers.value[dailyWorkerIndex].photoUrl = photoUrl
            console.log('📸 Updated dailyWorker in memory')
          }
          
          // IMPORTANT: Reload workers from server AFTER successful upload to ensure data persistence
          await loadWorkers()
          
          // Update dailyWorkers again after reload to ensure photo is displayed
          if (dateStr === selectedDate.value?.format('YYYY-MM-DD')) {
            const reloadedWorker = workers.value.find(w => w.id === selectedWorkerForPhoto.value.id)
            if (reloadedWorker) {
              const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === selectedWorkerForPhoto.value.id)
              if (dailyWorkerIndex !== -1) {
                dailyWorkers.value[dailyWorkerIndex].photo = reloadedWorker.photo || reloadedWorker.photoUrl
                dailyWorkers.value[dailyWorkerIndex].photoUrl = reloadedWorker.photoUrl || reloadedWorker.photo
              }
            }
          }
          
          message.destroy()
          message.success(`Successfully uploaded photo for ${selectedWorkerForPhoto.value.name}`)
          console.log('✅ Photo upload completed successfully')
        } else {
          message.destroy()
          throw new Error('Upload failed: No data returned')
        }
      } catch (error) {
        message.destroy()
        console.error('❌ Failed to upload photo:', error)
        console.error('Error details:', error.response || error.message)
        message.error(`Failed to upload photo: ${error.message || 'Unknown error'}`)
        // Don't close modal if photo upload fails
        return
      }
    }
    
    // Success message is already shown after API call
  } catch (error) {
    console.error('Error in confirmDailyManagement:', error)
    message.error(error.message || 'Failed to update schedule')
  }
  
  // Reset forms
  scheduleForm.value = {
    date: null,
    morningShift: [],
    eveningShift: [],
    nightShift: [],
    notes: ''
  }
  photoForm.value = {
    date: null
  }
  selectedWorkerForPhoto.value = null
  fileList.value = []
  
  dailyManagementModalVisible.value = false
}

const confirmScheduleUpdate = async () => {
  if (!scheduleForm.value.date || scheduleForm.value.selectedWorkers.length === 0) {
    message.warning('Please select a date and at least one worker')
    return
  }
  
  try {
    const dateStr = scheduleForm.value.date.format('YYYY-MM-DD')
    dailySchedules.value[dateStr] = scheduleForm.value.selectedWorkers
    
    // Update daily workers if it's the selected date
    if (dateStr === selectedDate.value?.format('YYYY-MM-DD')) {
      updateDailyWorkers(dateStr)
    }
    
    message.success('Daily schedule updated successfully!')
    scheduleModalVisible.value = false
    
    // Reset form
    scheduleForm.value = {
      date: null,
      morningShift: [],
      eveningShift: [],
      nightShift: [],
      notes: ''
    }
  } catch (error) {
    message.error('Failed to update schedule')
  }
}

const onDateChange = async (date) => {
  if (date) {
    const dateStr = date.format('YYYY-MM-DD')
    await loadDailySchedule(dateStr)
  }
}

const updateDailyWorkers = (dateStr) => {
  const workerIds = dailySchedules.value[dateStr] || []
  // Filter workers and maintain their photo data
  dailyWorkers.value = workers.value.filter(worker => workerIds.includes(worker.id))
}

const formatDate = (date) => {
  if (!date) return 'Today'
  return date.format('YYYY-MM-DD')
}

// Shift management functions
const getFilteredDailyWorkers = () => {
  if (!dailyWorkers.value || dailyWorkers.value.length === 0) {
    return []
  }
  
  // Filter workers based on selected shift
  // Check if worker is assigned to the selected shift
  const dateStr = selectedDate.value?.format('YYYY-MM-DD')
  if (!dateStr) return []
  
  // For now, show all daily workers for the selected shift
  // In a real implementation, this would filter based on shift assignments
  return dailyWorkers.value
}

const onShiftChange = () => {
  // Update filtered workers when shift selection changes
  // This will trigger re-render with the new filtered list
}

const getShiftInfo = () => {
  const shiftTimeSettings = JSON.parse(localStorage.getItem('shiftTimeSettings') || '{}')
  
  const defaultSettings = {
    morning: { start: '06:00', end: '14:00' },
    afternoon: { start: '14:00', end: '22:00' },
    night: { start: '22:00', end: '06:00' }
  }
  
  const settings = {
    morning: shiftTimeSettings.morning || defaultSettings.morning,
    afternoon: shiftTimeSettings.afternoon || defaultSettings.afternoon,
    night: shiftTimeSettings.night || defaultSettings.night
  }
  
  const shiftInfo = {
    morning: {
      title: 'Morning Shift',
      description: `Covers morning care activities (${settings.morning.start} - ${settings.morning.end}), breakfast assistance, and morning medications`
    },
    afternoon: {
      title: 'Afternoon Shift',
      description: `Handles afternoon activities (${settings.afternoon.start} - ${settings.afternoon.end}), lunch assistance, and afternoon care`
    },
    night: {
      title: 'Night Shift',
      description: `Provides overnight care (${settings.night.start} - ${settings.night.end}), evening medications, and night monitoring`
    }
  }
  
  return shiftInfo[selectedShift.value] || shiftInfo.morning
}


// Shift time settings functions
const showShiftTimeSettingsModal = () => {
  // Load current settings from localStorage
  const savedSettings = JSON.parse(localStorage.getItem('shiftTimeSettings') || '{}')
  
  // Set form values with current settings or defaults
  shiftTimeForm.value = {
    morningStart: savedSettings.morning?.start ? dayjs(savedSettings.morning.start, 'HH:mm') : dayjs('06:00', 'HH:mm'),
    morningEnd: savedSettings.morning?.end ? dayjs(savedSettings.morning.end, 'HH:mm') : dayjs('14:00', 'HH:mm'),
    afternoonStart: savedSettings.afternoon?.start ? dayjs(savedSettings.afternoon.start, 'HH:mm') : dayjs('14:00', 'HH:mm'),
    afternoonEnd: savedSettings.afternoon?.end ? dayjs(savedSettings.afternoon.end, 'HH:mm') : dayjs('22:00', 'HH:mm'),
    nightStart: savedSettings.night?.start ? dayjs(savedSettings.night.start, 'HH:mm') : dayjs('22:00', 'HH:mm'),
    nightEnd: savedSettings.night?.end ? dayjs(savedSettings.night.end, 'HH:mm') : dayjs('06:00', 'HH:mm')
  }
  
  shiftTimeSettingsModalVisible.value = true
}

const confirmShiftTimeSettings = () => {
  // Validate that all times are set
  if (!shiftTimeForm.value.morningStart || !shiftTimeForm.value.morningEnd ||
      !shiftTimeForm.value.afternoonStart || !shiftTimeForm.value.afternoonEnd ||
      !shiftTimeForm.value.nightStart || !shiftTimeForm.value.nightEnd) {
    message.error('Please set all shift times')
    return
  }
  
  // Save settings to localStorage
  const settings = {
    morning: {
      start: shiftTimeForm.value.morningStart.format('HH:mm'),
      end: shiftTimeForm.value.morningEnd.format('HH:mm')
    },
    afternoon: {
      start: shiftTimeForm.value.afternoonStart.format('HH:mm'),
      end: shiftTimeForm.value.afternoonEnd.format('HH:mm')
    },
    night: {
      start: shiftTimeForm.value.nightStart.format('HH:mm'),
      end: shiftTimeForm.value.nightEnd.format('HH:mm')
    }
  }
  
  localStorage.setItem('shiftTimeSettings', JSON.stringify(settings))
  shiftTimeSettings.value = settings
  
  message.success('Shift time settings saved successfully!')
  shiftTimeSettingsModalVisible.value = false
}

onMounted(async () => {
  loading.value = true
  
  try {
    // Get current user info
    const userInfo = await getMe()
    currentUser.value = userInfo.data
    organizationId.value = userInfo.data?.organizationId || 'default-org'
    
    // Load workers from API
    await loadWorkers()
    
    // Set today as default date
    selectedDate.value = dayjs()
    const todayStr = selectedDate.value.format('YYYY-MM-DD')
    
    // Load daily schedule for today
    await loadDailySchedule(todayStr)
    
    // Load shift time settings from localStorage
    const savedSettings = JSON.parse(localStorage.getItem('shiftTimeSettings') || '{}')
    if (Object.keys(savedSettings).length > 0) {
      shiftTimeSettings.value = savedSettings
    }
    
  } catch (error) {
    console.error('Failed to load worker management data:', error)
    message.error('Failed to load worker data')
  } finally {
    loading.value = false
  }
})

// Load workers from API
const loadWorkers = async () => {
  try {
    // If user is a manager, get workers bound to this manager
    let response
    if (currentUser.value?.role === 'manager' && currentUser.value?.id) {
      // Manager should see only workers bound to them
      console.log('🔍 Manager loading workers - Manager ID:', currentUser.value.id, 'Role:', currentUser.value.role)
      response = await getWorkersByManagerId(currentUser.value.id)
      console.log('📥 API Response:', response)
      console.log('📊 Workers data:', response.data)
    } else if (organizationId.value && organizationId.value !== 'default-org') {
      // For other roles (POA, etc.), use organization-specific API
      response = await getWorkersByOrganization(organizationId.value)
    } else {
      // Fallback to all workers if no organization ID
      response = await getAllWorkers()
    }
    
    // Map backend photoUrl to frontend photo field for consistency
    workers.value = (response.data || []).map(worker => {
      // Ensure photo field is set from photoUrl if photo is not already set
      if (!worker.photo && worker.photoUrl) {
        worker.photo = worker.photoUrl
      }
      // Also ensure photoUrl is set from photo for backwards compatibility
      if (!worker.photoUrl && worker.photo) {
        worker.photoUrl = worker.photo
      }
      
      // Convert photoUrl to full URL if it's a relative path
      // Use API endpoint for more reliable file serving
      if (worker.photoUrl && worker.photoUrl.startsWith('/uploads/')) {
        const API_BASE_URL = getBaseUrl()
        // Use API endpoint instead of direct static resource access
        const fullPhotoUrl = `${API_BASE_URL}/api/files/serve?path=${encodeURIComponent(worker.photoUrl)}`
        worker.photo = fullPhotoUrl
        worker.photoUrl = fullPhotoUrl
      }
      
      return worker
    })
    
    console.log('✅ Loaded workers array:', workers.value)
    console.log('📈 Workers count:', workers.value.length)
    console.log('📋 Workers details:', workers.value.map(w => ({ 
      id: w.id, 
      name: w.name, 
      workerId: w.workerId, 
      managerId: w.managerId, 
      status: w.status,
      photo: w.photo,
      photoUrl: w.photoUrl
    })))
    
    // Initialize available workers (all active workers)
    availableWorkers.value = workers.value.filter(worker => worker.status === 'active' || worker.status === 'Active')
    
    console.log('✅ Loaded workers:', workers.value.length, 'for user role:', currentUser.value?.role, 'organization:', organizationId.value)
    console.log('✅ Available workers:', availableWorkers.value.length)
    console.log('✅ Available workers data:', availableWorkers.value)
    
  } catch (error) {
    console.error('❌ Failed to load workers:', error)
    console.error('❌ Error details:', error.response?.data || error.message)
    message.error('Failed to load workers: ' + (error.message || 'Unknown error'))
  }
}

// Load daily schedule for a specific date
const loadDailySchedule = async (dateStr) => {
  try {
    if (organizationId.value) {
      const response = await getDailySchedule(organizationId.value, dateStr)
      const scheduledWorkers = response.data || []
      
      // Update daily workers with scheduled workers
      dailyWorkers.value = scheduledWorkers
    }
  } catch (error) {
    console.error('Failed to load daily schedule:', error)
    // If no schedule exists for this date, dailyWorkers will be empty
    dailyWorkers.value = []
  }
}
</script>

<style scoped>
.worker-management-page {
  max-width: 1200px;
  margin: 0 auto;
}

.ant-upload-drag-icon {
  font-size: 48px;
  color: #1890ff;
}

.ant-upload-text {
  font-size: 16px;
  color: #666;
}

.ant-upload-hint {
  font-size: 14px;
  color: #999;
}

.selected-worker {
  border: 2px solid #1890ff !important;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3) !important;
}

.selected-worker .ant-card-body {
  background-color: #f6ffed;
}
</style>
