<template>
  <div class="tasks-page">
    <!-- Date Selection Section -->
    <div style="margin-bottom: 30px;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h3 style="margin: 0; color: #1890ff;">
          <CheckSquareOutlined style="margin-right: 8px;" />
          Today's Tasks - {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}
        </h3>
        <a-date-picker v-model:value="selectedDate" @change="onDateChange" style="width: 200px;" placeholder="Select date" />
      </div>
    </div>

    <!-- POA/Worker Simple Task View -->
    <div v-if="!isManager">
    <a-card>
      <template #title>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span>{{ isPOA ? 'Task Review' : 'My Tasks' }}</span>
              <a-tooltip :title="isPOA ? 'Review tasks completed by workers and confirm completion. You can also request task adjustments from the manager.' : 'View your assigned tasks for today. You can mark tasks as completed.'">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
          </div>
            <div v-if="isPOA" style="display: flex; align-items: center; gap: 8px;">
              <a-button type="primary" @click="showRequestTaskModal">
                <PlusOutlined />
                Request Task
              </a-button>
              <a-tooltip title="Request task adjustments or new tasks from the manager">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
            </div>
          </div>
        </template>
        
        <a-table
          :columns="simpleTaskColumns"
          :data-source="getMyTasks()"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'priority'">
              <a-tag :color="getPriorityColor(record.priority)">
                {{ getPriorityText(record.priority) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            
          </template>
        </a-table>
      </a-card>

      <!-- My Requests Section (Only for POA) -->
      <a-card v-if="isPOA" style="margin-top: 24px;">
        <template #title>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span>My Requests</span>
            <a-tooltip title="View the status of your submitted requests and manager responses. You can see if your requests were approved, rejected, or are still pending.">
              <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="myRequestsColumns"
          :data-source="myRequests"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getRequestStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'managerResponse'">
              <div v-if="record.status === 'Approved'" style="color: #52c41a; font-weight: bold;">
                ✓ Approved
                <div v-if="record.approvalReason" style="font-size: 12px; color: #666; margin-top: 4px;">
                  {{ record.approvalReason }}
                </div>
              </div>
              <div v-else-if="record.status === 'Rejected'" style="color: #ff4d4f; font-weight: bold;">
                ✗ Rejected
                <div v-if="record.rejectionReason" style="font-size: 12px; color: #666; margin-top: 4px;">
                  {{ record.rejectionReason }}
                </div>
              </div>
              <div v-else style="color: #999;">
                Waiting for manager response
              </div>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>

    <!-- Manager Full Task Management View -->
    <div v-if="isManager">
      <!-- Today's Tasks Section -->
      <a-card style="margin-bottom: 24px;">
        <template #title>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span>All Tasks</span>
              <a-tooltip title="View and manage all tasks for today. You can create new tasks, edit existing ones, complete tasks, or delete them.">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
            </div>
            <a-tooltip title="Create a new task and assign it to a worker. Set priority level, due date, and description.">
              <a-button type="primary" @click="showCreateTaskModal">
                <PlusOutlined />
                Create Task
              </a-button>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="taskColumns"
          :data-source="todayTasks"
          :pagination="false"
                  size="small" 
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'assignedTo'">
              <span v-if="record.assignedTo">{{ record.assignedTo }}</span>
              <a-tag v-else color="orange">Unassigned</a-tag>
            </template>
            <template v-if="column.key === 'priority'">
              <a-tag :color="getPriorityColor(record.priority)">
                {{ getPriorityText(record.priority) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="editTask(record)">
                  Edit
                </a-button>
                <a-button 
                  size="small" 
                  type="primary" 
                  @click="handleCompleteTask(record)"
                  :disabled="record.status === 'Completed'"
                  style="background-color: #52c41a; border-color: #52c41a;"
                >
                  Complete
                </a-button>
                <a-button size="small" danger @click="handleDeleteTask(record)">
                  Delete
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>

      <!-- Recurring Tasks Section -->
      <a-card style="margin-bottom: 24px;">
        <template #title>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span>Recurring Tasks</span>
              <a-tooltip title="Manage recurring task templates. Create templates for tasks that repeat daily, weekly, or monthly. The system will automatically generate tasks based on these templates.">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
            </div>
            <a-tooltip title="Create a new recurring task template. Set the frequency (daily, weekly, monthly) and the system will automatically generate tasks.">
              <a-button type="primary" @click="showCreateRecurringTaskModal">
                <PlusOutlined />
                Create Recurring Task
              </a-button>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="recurringTaskColumns"
          :data-source="recurringTasks"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'assignedTo'">
              <span v-if="record.assignedTo">{{ record.assignedTo }}</span>
              <a-tag v-else color="orange">Unassigned</a-tag>
            </template>
            <template v-if="column.key === 'frequency'">
              <a-tag :color="getFrequencyColor(record.frequency)">
                {{ getFrequencyText(record.frequency, record.frequencyNumber) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="record.isActive ? 'green' : 'red'">
                {{ record.isActive ? 'Active' : 'Inactive' }}
              </a-tag>
            </template>
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="editRecurringTask(record)">
                  Edit
                </a-button>
                <a-button 
                  size="small" 
                  :type="record.isActive ? 'default' : 'primary'"
                  @click="toggleRecurringTask(record)"
                >
                  {{ record.isActive ? 'Deactivate' : 'Activate' }}
                </a-button>
                <a-button size="small" danger @click="handleDeleteRecurringTask(record)">
                  Delete
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>


      <!-- Request Change Section -->
      <a-card>
        <template #title>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span>Request Change</span>
            <a-tooltip title="Review and manage change requests from family members. You can approve or reject requests for task modifications, worker changes, or other adjustments.">
              <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="requestColumns"
          :data-source="changeRequests"
          :pagination="false"
                  size="small" 
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getRequestStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="viewRequestDetails(record)">
                  View Details
                </a-button>
                <a-space v-if="record.status === 'Pending'">
                  <a-button size="small" type="primary" @click="showRequestConfirmModal(record, 'approve')">
                    Approve
                  </a-button>
                  <a-button size="small" danger @click="showRequestConfirmModal(record, 'reject')">
                    Reject
                  </a-button>
                </a-space>
                <span v-else style="color: #999;">{{ record.status }}</span>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>

      <!-- Create Recurring Task Modal -->
      <a-modal
        v-model:open="createRecurringTaskModalVisible"
        title="Create Recurring Task Template"
        width="700px"
        @ok="confirmCreateRecurringTask"
        @cancel="createRecurringTaskModalVisible = false"
      >
        <a-form :model="createRecurringTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="createRecurringTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="createRecurringTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To">
            <a-select v-model:value="createRecurringTaskForm.assignedTo" placeholder="Select worker (optional)" style="width: 100%;" allowClear>
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Recurrence Frequency" required>
            <a-row :gutter="8">
              <a-col :span="8">
                <a-input-number 
                  v-model:value="createRecurringTaskForm.frequencyNumber" 
                  :min="1" 
                  :max="12" 
                  style="width: 100%;" 
                  placeholder="Number"
                />
              </a-col>
              <a-col :span="16">
                <a-select v-model:value="createRecurringTaskForm.frequency" placeholder="Select frequency" style="width: 100%;" @change="onFrequencyChange">
                  <a-select-option value="daily">Daily</a-select-option>
                  <a-select-option value="weekly">Weekly</a-select-option>
                  <a-select-option value="monthly">Monthly</a-select-option>
                  <a-select-option value="yearly">Yearly</a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-item>
          
          <!-- Daily options -->
          <div v-if="createRecurringTaskForm.frequency === 'daily'">
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="createRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Weekly options -->
          <div v-if="createRecurringTaskForm.frequency === 'weekly'">
            <a-form-item label="Day of Week" required>
              <a-select v-model:value="createRecurringTaskForm.dayOfWeek" placeholder="Select day" style="width: 100%;">
                <a-select-option value="monday">Monday</a-select-option>
                <a-select-option value="tuesday">Tuesday</a-select-option>
                <a-select-option value="wednesday">Wednesday</a-select-option>
                <a-select-option value="thursday">Thursday</a-select-option>
                <a-select-option value="friday">Friday</a-select-option>
                <a-select-option value="saturday">Saturday</a-select-option>
                <a-select-option value="sunday">Sunday</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="createRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Monthly options -->
          <div v-if="createRecurringTaskForm.frequency === 'monthly'">
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="createRecurringTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="createRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Yearly options -->
          <div v-if="createRecurringTaskForm.frequency === 'yearly'">
            <a-form-item label="Month" required>
              <a-select v-model:value="createRecurringTaskForm.month" placeholder="Select month" style="width: 100%;">
                <a-select-option value="january">January</a-select-option>
                <a-select-option value="february">February</a-select-option>
                <a-select-option value="march">March</a-select-option>
                <a-select-option value="april">April</a-select-option>
                <a-select-option value="may">May</a-select-option>
                <a-select-option value="june">June</a-select-option>
                <a-select-option value="july">July</a-select-option>
                <a-select-option value="august">August</a-select-option>
                <a-select-option value="september">September</a-select-option>
                <a-select-option value="october">October</a-select-option>
                <a-select-option value="november">November</a-select-option>
                <a-select-option value="december">December</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="createRecurringTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="createRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <a-form-item label="Start Date" required>
            <a-date-picker v-model:value="createRecurringTaskForm.startDate" style="width: 100%;" placeholder="Select start date" />
          </a-form-item>
          <a-form-item label="End Date (Optional)">
            <a-date-picker v-model:value="createRecurringTaskForm.endDate" style="width: 100%;" placeholder="Select end date (leave empty for no end)" />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- Edit Recurring Task Modal -->
      <a-modal
        v-model:open="editRecurringTaskModalVisible"
        title="Edit Recurring Task Template"
        width="700px"
        @ok="confirmEditRecurringTask"
        @cancel="editRecurringTaskModalVisible = false"
      >
        <a-form :model="editRecurringTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="editRecurringTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="editRecurringTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To">
            <a-select v-model:value="editRecurringTaskForm.assignedTo" placeholder="Select worker (optional)" style="width: 100%;" allowClear>
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Recurrence Frequency" required>
            <a-row :gutter="8">
              <a-col :span="8">
                <a-input-number 
                  v-model:value="editRecurringTaskForm.frequencyNumber" 
                  :min="1" 
                  :max="12" 
                  style="width: 100%;" 
                  placeholder="Number"
                />
              </a-col>
              <a-col :span="16">
                <a-select v-model:value="editRecurringTaskForm.frequency" placeholder="Select frequency" style="width: 100%;" @change="onEditFrequencyChange">
                  <a-select-option value="daily">Daily</a-select-option>
                  <a-select-option value="weekly">Weekly</a-select-option>
                  <a-select-option value="monthly">Monthly</a-select-option>
                  <a-select-option value="yearly">Yearly</a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-item>
          
          <!-- Daily options -->
          <div v-if="editRecurringTaskForm.frequency === 'daily'">
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="editRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Weekly options -->
          <div v-if="editRecurringTaskForm.frequency === 'weekly'">
            <a-form-item label="Day of Week" required>
              <a-select v-model:value="editRecurringTaskForm.dayOfWeek" placeholder="Select day" style="width: 100%;">
                <a-select-option value="monday">Monday</a-select-option>
                <a-select-option value="tuesday">Tuesday</a-select-option>
                <a-select-option value="wednesday">Wednesday</a-select-option>
                <a-select-option value="thursday">Thursday</a-select-option>
                <a-select-option value="friday">Friday</a-select-option>
                <a-select-option value="saturday">Saturday</a-select-option>
                <a-select-option value="sunday">Sunday</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="editRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Monthly options -->
          <div v-if="editRecurringTaskForm.frequency === 'monthly'">
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="editRecurringTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="editRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Yearly options -->
          <div v-if="editRecurringTaskForm.frequency === 'yearly'">
            <a-form-item label="Month" required>
              <a-select v-model:value="editRecurringTaskForm.month" placeholder="Select month" style="width: 100%;">
                <a-select-option value="january">January</a-select-option>
                <a-select-option value="february">February</a-select-option>
                <a-select-option value="march">March</a-select-option>
                <a-select-option value="april">April</a-select-option>
                <a-select-option value="may">May</a-select-option>
                <a-select-option value="june">June</a-select-option>
                <a-select-option value="july">July</a-select-option>
                <a-select-option value="august">August</a-select-option>
                <a-select-option value="september">September</a-select-option>
                <a-select-option value="october">October</a-select-option>
                <a-select-option value="november">November</a-select-option>
                <a-select-option value="december">December</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="editRecurringTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="editRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <a-form-item label="Start Date" required>
            <a-date-picker v-model:value="editRecurringTaskForm.startDate" style="width: 100%;" placeholder="Select start date" />
          </a-form-item>
          <a-form-item label="End Date (Optional)">
            <a-date-picker v-model:value="editRecurringTaskForm.endDate" style="width: 100%;" placeholder="Select end date (leave empty for no end)" />
          </a-form-item>
          <a-form-item label="Status">
            <a-switch v-model:checked="editRecurringTaskForm.isActive" checked-children="Active" un-checked-children="Inactive" />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- Create Task Modal -->
      <a-modal
        v-model:open="createTaskModalVisible"
        title="Create New Task"
        width="600px"
        @ok="confirmCreateTask"
        @cancel="createTaskModalVisible = false"
      >
        <a-form :model="createTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="createTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="createTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To">
            <a-select v-model:value="createTaskForm.assignedTo" placeholder="Select worker (optional)" style="width: 100%;" allowClear>
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Priority" required>
            <a-select v-model:value="createTaskForm.priority" placeholder="Select priority" style="width: 100%;">
              <a-select-option value="normal">
                <a-tag color="green">Normal</a-tag>
              </a-select-option>
              <a-select-option value="urgent">
                <a-tag color="orange">Urgent</a-tag>
              </a-select-option>
              <a-select-option value="very-urgent">
                <a-tag color="red">Very Urgent</a-tag>
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Due Date" required>
            <a-date-picker v-model:value="createTaskForm.dueDate" style="width: 100%;" placeholder="Select due date" />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- Edit Task Modal -->
      <a-modal
        v-model:open="editTaskModalVisible"
        title="Edit Task"
        width="600px"
        @ok="confirmEditTask"
        @cancel="editTaskModalVisible = false"
      >
        <a-form :model="editTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="editTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="editTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To">
            <a-select v-model:value="editTaskForm.assignedTo" placeholder="Select worker (optional)" style="width: 100%;" allowClear>
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Priority" required>
            <a-select v-model:value="editTaskForm.priority" placeholder="Select priority" style="width: 100%;">
              <a-select-option value="normal">
                <a-tag color="green">Normal</a-tag>
              </a-select-option>
              <a-select-option value="urgent">
                <a-tag color="orange">Urgent</a-tag>
              </a-select-option>
              <a-select-option value="very-urgent">
                <a-tag color="red">Very Urgent</a-tag>
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Status" required>
            <a-select v-model:value="editTaskForm.status" placeholder="Select status" style="width: 100%;">
              <a-select-option value="Pending">Pending</a-select-option>
              <a-select-option value="In Progress">In Progress</a-select-option>
              <a-select-option value="Completed">Completed</a-select-option>
              <a-select-option value="Cancelled">Cancelled</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Due Date" required>
            <a-date-picker v-model:value="editTaskForm.dueDate" style="width: 100%;" placeholder="Select due date" />
          </a-form-item>
        </a-form>
      </a-modal>


    </div>
    <!-- End Manager View -->

    <!-- POA Request Task Modal (Outside Manager View) -->
    <a-modal
      v-if="isPOA"
      v-model:open="requestTaskModalVisible"
      title="Request Task Adjustment"
      width="600px"
      @ok="confirmRequestTask"
      @cancel="requestTaskModalVisible = false"
    >
      <a-form :model="requestTaskForm" layout="vertical">
        <a-form-item label="Request Type" required>
          <a-select v-model:value="requestTaskForm.requestType" placeholder="Select request type" style="width: 100%;" @change="onRequestTypeChange">
            <a-select-option value="new">Add New Task</a-select-option>
            <a-select-option value="recurring">Add Recurring Task</a-select-option>
            <a-select-option value="modify">Edit Task</a-select-option>
            <a-select-option value="remove">Remove Task</a-select-option>
            <a-select-option value="reschedule">Reschedule Task</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="Task Title" required>
          <a-input v-model:value="requestTaskForm.title" placeholder="Enter task title" />
        </a-form-item>
        <a-form-item label="Description">
          <a-textarea v-model:value="requestTaskForm.description" placeholder="Enter task description or modification details" :rows="3" />
        </a-form-item>
        <a-form-item label="Priority">
          <a-select v-model:value="requestTaskForm.priority" placeholder="Select priority" style="width: 100%;">
            <a-select-option value="normal">Normal</a-select-option>
            <a-select-option value="urgent">Urgent</a-select-option>
            <a-select-option value="very-urgent">Very Urgent</a-select-option>
          </a-select>
        </a-form-item>
        
        <!-- Recurring Task Frequency (only show when request type is recurring) -->
        <div v-if="requestTaskForm.requestType === 'recurring'">
          <a-form-item label="Recurrence Frequency" required>
            <a-row :gutter="8">
              <a-col :span="8">
                <a-input-number 
                  v-model:value="requestTaskForm.frequencyNumber" 
                  :min="1" 
                  :max="12" 
                  style="width: 100%;" 
                  placeholder="Number"
                />
              </a-col>
              <a-col :span="16">
                <a-select v-model:value="requestTaskForm.frequency" placeholder="Select frequency" style="width: 100%;">
                  <a-select-option value="daily">Daily</a-select-option>
                  <a-select-option value="weekly">Weekly</a-select-option>
                  <a-select-option value="monthly">Monthly</a-select-option>
                  <a-select-option value="yearly">Yearly</a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-item>
          
          <!-- Daily options -->
          <div v-if="requestTaskForm.frequency === 'daily'">
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="requestTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Weekly options -->
          <div v-if="requestTaskForm.frequency === 'weekly'">
            <a-form-item label="Day of Week" required>
              <a-select v-model:value="requestTaskForm.dayOfWeek" placeholder="Select day" style="width: 100%;">
                <a-select-option value="monday">Monday</a-select-option>
                <a-select-option value="tuesday">Tuesday</a-select-option>
                <a-select-option value="wednesday">Wednesday</a-select-option>
                <a-select-option value="thursday">Thursday</a-select-option>
                <a-select-option value="friday">Friday</a-select-option>
                <a-select-option value="saturday">Saturday</a-select-option>
                <a-select-option value="sunday">Sunday</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="requestTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Monthly options -->
          <div v-if="requestTaskForm.frequency === 'monthly'">
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="requestTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="requestTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Yearly options -->
          <div v-if="requestTaskForm.frequency === 'yearly'">
            <a-form-item label="Month" required>
              <a-select v-model:value="requestTaskForm.month" placeholder="Select month" style="width: 100%;">
                <a-select-option value="january">January</a-select-option>
                <a-select-option value="february">February</a-select-option>
                <a-select-option value="march">March</a-select-option>
                <a-select-option value="april">April</a-select-option>
                <a-select-option value="may">May</a-select-option>
                <a-select-option value="june">June</a-select-option>
                <a-select-option value="july">July</a-select-option>
                <a-select-option value="august">August</a-select-option>
                <a-select-option value="september">September</a-select-option>
                <a-select-option value="october">October</a-select-option>
                <a-select-option value="november">November</a-select-option>
                <a-select-option value="december">December</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="requestTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="requestTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <a-form-item label="Start Date" required>
            <a-date-picker v-model:value="requestTaskForm.startDate" style="width: 100%;" placeholder="Select start date" />
          </a-form-item>
          <a-form-item label="End Date (Optional)">
            <a-date-picker v-model:value="requestTaskForm.endDate" style="width: 100%;" placeholder="Select end date (leave empty for no end)" />
          </a-form-item>
        </div>
        
        <a-form-item label="Reason for Request" required>
          <a-textarea v-model:value="requestTaskForm.reason" placeholder="Explain why this task adjustment is needed" :rows="2" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- POA Task Confirmation Modal（ -->
    <a-modal
      v-if="isPOA"
      v-model:open="taskConfirmModalVisible"
      :title="confirmModalTitle"
      width="400px"
      @ok="handleTaskConfirmation"
      @cancel="taskConfirmModalVisible = false"
    >
      <p>{{ confirmModalMessage }}</p>
      <a-form-item label="Reason (Optional)">
        <a-textarea v-model:value="confirmReason" placeholder="Enter reason for approval/rejection" :rows="2" />
      </a-form-item>
    </a-modal>

    <!-- Request Details Modal -->
    <a-modal
      v-model:open="requestDetailsModalVisible"
      title="Request Details"
      width="700px"
      :footer="null"
    >
      <div v-if="selectedRequest">
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="Requester">
            {{ selectedRequest.requester }}
          </a-descriptions-item>
          <a-descriptions-item label="Task Title">
            {{ selectedRequest.taskTitle }}
          </a-descriptions-item>
          <a-descriptions-item label="Request Type">
            <a-tag :color="getRequestTypeColor(selectedRequest.requestType)">
              {{ getRequestTypeText(selectedRequest.requestType) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring'" label="Frequency">
            <a-tag :color="getFrequencyColor(selectedRequest.frequency)">
              {{ getFrequencyText(selectedRequest.frequency, selectedRequest.frequencyNumber) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.timeOfDay" label="Time">
            {{ selectedRequest.timeOfDay }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.dayOfWeek" label="Day of Week">
            {{ selectedRequest.dayOfWeek }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.dayOfMonth" label="Day of Month">
            {{ selectedRequest.dayOfMonth }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.startDate" label="Start Date">
            {{ selectedRequest.startDate }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.endDate" label="End Date">
            {{ selectedRequest.endDate }}
          </a-descriptions-item>
          <a-descriptions-item label="Reason">
            {{ selectedRequest.reason }}
          </a-descriptions-item>
          <a-descriptions-item label="Status">
            <a-tag :color="getRequestStatusColor(selectedRequest.status)">
              {{ selectedRequest.status }}
            </a-tag>
          </a-descriptions-item>
        </a-descriptions>
        
        <div style="margin-top: 20px; text-align: right;">
          <a-space>
            <a-button @click="requestDetailsModalVisible = false">
              Close
            </a-button>
            <a-button 
              v-if="selectedRequest.status === 'Pending'"
              type="primary" 
              @click="approveFromDetails"
            >
              Approve
            </a-button>
            <a-button 
              v-if="selectedRequest.status === 'Pending'"
              danger 
              @click="rejectFromDetails"
            >
              Reject
            </a-button>
          </a-space>
        </div>
      </div>
    </a-modal>

    <!-- Request Confirmation Modal -->
    <a-modal
      v-model:open="requestConfirmModalVisible"
      :title="requestConfirmModalTitle"
      width="400px"
      @ok="handleRequestConfirmation"
      @cancel="requestConfirmModalVisible = false"
    >
      <p>{{ requestConfirmModalMessage }}</p>
      <a-form-item label="Reason (Optional)">
        <a-textarea v-model:value="requestConfirmReason" placeholder="Enter reason for approval/rejection" :rows="2" />
      </a-form-item>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { CheckSquareOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { getMe } from '../services/userService'
import { 
  getAllTasks, 
  getTasksByPatient, 
  getTasksByWorker, 
  createTask, 
  updateTask, 
  deleteTask as deleteTaskAPI,
  assignTask,
  completeTask as completeTaskAPI,
  approveTask,
  rejectTask,
  getRecurringTasks,
  createRecurringTask,
  updateRecurringTask,
  deleteRecurringTask as deleteRecurringTaskAPI,
  createTaskRequest,
  getPendingTaskRequests,
  getMyTaskRequests,
  approveTaskRequest,
  rejectTaskRequest
} from '../services/taskService'
import { 
  getAllWorkers,
  getWorkersByOrganization,
  getWorkersByManagerId
} from '../services/workerService'

// Reactive data
const selectedDate = ref(null)
const createTaskModalVisible = ref(false)
const editTaskModalVisible = ref(false)
const currentUser = ref(null)
const requestTaskModalVisible = ref(false)
const taskConfirmModalVisible = ref(false)
const confirmModalTitle = ref('')
const confirmModalMessage = ref('')
const confirmReason = ref('')
const pendingTaskAction = ref(null)
const createRecurringTaskModalVisible = ref(false)
const editRecurringTaskModalVisible = ref(false)

// Request confirmation modal
const requestConfirmModalVisible = ref(false)
const requestConfirmModalTitle = ref('')
const requestConfirmModalMessage = ref('')
const requestConfirmReason = ref('')
const pendingRequestAction = ref(null)

// Request details modal
const requestDetailsModalVisible = ref(false)
const selectedRequest = ref(null)

// Computed properties
const isManager = computed(() => currentUser.value?.role === 'manager')
const isPOA = computed(() => currentUser.value?.role === 'poa')
const isWorker = computed(() => currentUser.value?.role === 'worker')

// Forms
const createTaskForm = ref({
  title: '',
  description: '',
  assignedTo: '',
  priority: '',
  dueDate: null
})

const editTaskForm = ref({
  id: null,
  title: '',
  description: '',
  assignedTo: '',
  priority: '',
  status: '',
  dueDate: null
})

const requestTaskForm = ref({
  requestType: '',
  title: '',
  description: '',
  priority: '',
  reason: '',
  // Recurring task fields
  frequency: '',
  frequencyNumber: 1,
  timeOfDay: null,
  dayOfWeek: '',
  dayOfMonth: null,
  month: '',
  startDate: null,
  endDate: null
})

const createRecurringTaskForm = ref({
  title: '',
  description: '',
  assignedTo: '',
  frequency: '',
  frequencyNumber: 1,
  timeOfDay: null,
  dayOfWeek: '',
  dayOfMonth: null,
  month: '',
  startDate: null,
  endDate: null
})

const editRecurringTaskForm = ref({
  id: null,
  title: '',
  description: '',
  assignedTo: '',
  frequency: '',
  frequencyNumber: 1,
  timeOfDay: null,
  dayOfWeek: '',
  dayOfMonth: null,
  month: '',
  startDate: null,
  endDate: null,
  isActive: true
})

// Available workers - loaded from API
const availableWorkers = ref([])

// Today's tasks - loaded from API
const todayTasks = ref([])


// Change requests - loaded from API
const changeRequests = ref([])

// POA's own requests - loaded from API
const myRequests = ref([])

// Recurring tasks - loaded from API
const recurringTasks = ref([])

// Table columns
const taskColumns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'Assigned To', dataIndex: 'assignedTo', key: 'assignedTo' },
  { title: 'Priority', dataIndex: 'priority', key: 'priority' },
  { title: 'Due', dataIndex: 'dueDate', key: 'dueDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 120 }
]

const simpleTaskColumns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'Priority', dataIndex: 'priority', key: 'priority' },
  { title: 'Due', dataIndex: 'dueDate', key: 'dueDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' }
]


const requestColumns = [
  { title: 'Requester', dataIndex: 'requester', key: 'requester' },
  { title: 'Task', dataIndex: 'taskTitle', key: 'taskTitle' },
  { title: 'Request Type', dataIndex: 'requestType', key: 'requestType' },
  { title: 'Frequency', key: 'frequency', customRender: ({ record }) => {
    if (record.requestType === 'recurring' && record.frequency) {
      return getFrequencyText(record.frequency, record.frequencyNumber)
    }
    return '-'
  }},
  { title: 'Reason', dataIndex: 'reason', key: 'reason' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 120 }
]

const myRequestsColumns = [
  { title: 'Task', dataIndex: 'taskTitle', key: 'taskTitle' },
  { title: 'Request Type', dataIndex: 'requestType', key: 'requestType' },
  { title: 'Frequency', key: 'frequency', customRender: ({ record }) => {
    if (record.requestType === 'recurring' && record.frequency) {
      return getFrequencyText(record.frequency, record.frequencyNumber)
    }
    return '-'
  }},
  { title: 'Reason', dataIndex: 'reason', key: 'reason' },
  { title: 'Submitted Date', dataIndex: 'submittedDate', key: 'submittedDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Manager Response', key: 'managerResponse', width: 200 }
]


const recurringTaskColumns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'Assigned To', dataIndex: 'assignedTo', key: 'assignedTo' },
  { title: 'Frequency', key: 'frequency', customRender: ({ record }) => getFrequencyText(record.frequency, record.frequencyNumber) },
  { title: 'Schedule', key: 'schedule', customRender: ({ record }) => {
    const frequencyText = getFrequencyText(record.frequency, record.frequencyNumber)
    if (record.frequency === 'daily') {
      return `${frequencyText} at ${record.timeOfDay}`
    } else if (record.frequency === 'weekly') {
      return `${record.dayOfWeek} at ${record.timeOfDay} (${frequencyText})`
    } else if (record.frequency === 'monthly') {
      return `Day ${record.dayOfMonth} at ${record.timeOfDay} (${frequencyText})`
    } else if (record.frequency === 'yearly') {
      return `${record.month} ${record.dayOfMonth} at ${record.timeOfDay} (${frequencyText})`
    }
    return '-'
  }},
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 200 }
]

// Methods
const onDateChange = (date) => {
  selectedDate.value = date
  // Update tasks based on selected date
  updateTasksForDate(date)
}

const updateTasksForDate = (date) => {
  // Filter tasks for selected date
  const dateStr = date.format('YYYY-MM-DD')
  // In real app, this would fetch from API
  console.log('Loading tasks for date:', dateStr)
  
  // Generate tasks from recurring templates for the selected date
  generateTasksFromTemplates(date)
}

const showCreateTaskModal = () => {
  createTaskForm.value = {
    title: '',
    description: '',
    assignedTo: '',
    priority: '',
    dueDate: selectedDate.value || dayjs()
  }
  createTaskModalVisible.value = true
}

const confirmCreateTask = async () => {
  if (!createTaskForm.value.title || !createTaskForm.value.priority) {
    message.error('Please fill in all required fields')
    return
  }
  
  try {
    message.loading('Creating task...', 0)
    
    const taskData = {
      title: createTaskForm.value.title,
      description: createTaskForm.value.description,
      assignedTo: createTaskForm.value.assignedTo || null,
      assignedToId: createTaskForm.value.assignedTo || null, // Set assignedToId to worker user ID
      priority: createTaskForm.value.priority,
      dueDate: createTaskForm.value.dueDate.format('YYYY-MM-DD'),
      status: 'Pending',
      patientId: currentUser.value?.patientId || null,
      createdBy: currentUser.value?.id || null
    }
    
    const response = await createTask(taskData)
    
    if (response.data) {
      // Add to local tasks array
      const newTask = {
        ...response.data,
        id: response.data.id || Date.now()
      }
      todayTasks.value.push(newTask)
      
      message.destroy()
      message.success('Task created successfully!')
      createTaskModalVisible.value = false
      
      // Reset form
      createTaskForm.value = {
        title: '',
        description: '',
        assignedTo: '',
        priority: '',
        dueDate: null
      }
    }
  } catch (error) {
    message.destroy()
    console.error('Failed to create task:', error)
    message.error(error.message || 'Failed to create task')
  }
}

const editTask = (task) => {
  editTaskForm.value = {
    id: task.id,
    title: task.title,
    description: task.description,
    assignedTo: task.assignedTo,
    priority: task.priority,
    status: task.status,
    dueDate: dayjs(task.dueDate)
  }
  editTaskModalVisible.value = true
}

const confirmEditTask = async () => {
  try {
    message.loading('Updating task...', 0)
    
    const taskData = {
      id: editTaskForm.value.id,
      title: editTaskForm.value.title,
      description: editTaskForm.value.description,
      assignedTo: editTaskForm.value.assignedTo,
      priority: editTaskForm.value.priority,
      status: editTaskForm.value.status,
      dueDate: editTaskForm.value.dueDate.format('YYYY-MM-DD')
    }
    
    // Call API to update task (taskId is in taskData)
    const response = await updateTask(taskData)
    
    if (response.data) {
      const taskIndex = todayTasks.value.findIndex(t => t.id === editTaskForm.value.id)
      if (taskIndex !== -1) {
        todayTasks.value[taskIndex] = {
          ...todayTasks.value[taskIndex],
          ...response.data
        }
      }
      
      message.destroy()
      message.success('Task updated successfully!')
      editTaskModalVisible.value = false
    } else {
      message.destroy()
      message.error('Failed to update task')
    }
  } catch (error) {
    message.destroy()
    console.error('Failed to update task:', error)
    message.error(error.message || 'Failed to update task')
  }
}

const handleCompleteTask = async (task) => {
  try {
    message.loading('Completing task...', 0)
    
    const response = await completeTaskAPI(task.id)
    
    if (response.data) {
      const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
      if (taskIndex !== -1) {
        todayTasks.value[taskIndex].status = 'Completed'
      }
      
      message.destroy()
      message.success('Task completed successfully!')
    } else {
      message.destroy()
      message.error('Failed to complete task')
    }
  } catch (error) {
    message.destroy()
    console.error('Failed to complete task:', error)
    message.error(error.message || 'Failed to complete task')
  }
}

const handleDeleteTask = async (task) => {
  try {
    message.loading('Deleting task...', 0)
    
    const response = await deleteTaskAPI(task.id)
    
    if (response.data) {
      const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
      if (taskIndex !== -1) {
        todayTasks.value.splice(taskIndex, 1)
      }
      
      message.destroy()
      message.success('Task deleted successfully!')
    } else {
      message.destroy()
      message.error('Failed to delete task')
    }
  } catch (error) {
    message.destroy()
    console.error('Failed to delete task:', error)
    message.error(error.message || 'Failed to delete task')
  }
}


const getMyTasks = () => {
  // For POA/Worker, show tasks assigned to them
  // In a real app, this would be based on the current user's ID
  // For now, we'll show all tasks for demonstration
  return todayTasks.value
}

const showRequestTaskModal = () => {
  requestTaskForm.value = {
    requestType: '',
    title: '',
    description: '',
    priority: '',
    reason: '',
    // Recurring task fields
    frequency: '',
    frequencyNumber: 1,
    timeOfDay: null,
    dayOfWeek: '',
    dayOfMonth: null,
    month: '',
    startDate: null,
    endDate: null
  }
  requestTaskModalVisible.value = true
}

const onRequestTypeChange = () => {
  // Reset frequency-specific fields when request type changes
  requestTaskForm.value.frequency = ''
  requestTaskForm.value.frequencyNumber = 1
  requestTaskForm.value.timeOfDay = null
  requestTaskForm.value.dayOfWeek = ''
  requestTaskForm.value.dayOfMonth = null
  requestTaskForm.value.month = ''
  requestTaskForm.value.startDate = null
  requestTaskForm.value.endDate = null
}

const confirmRequestTask = async () => {
  try {
    message.loading('Submitting request...', 0)
    
    // Get current user info
    const userInfo = await getMe()
    if (!userInfo?.data) {
      throw new Error('User not authenticated')
    }
    
    // Prepare request data with frequency information if it's a recurring task
    const requestData = {
      requester: 'POA',
      taskTitle: requestTaskForm.value.title,
      description: requestTaskForm.value.description || '',
      requestType: requestTaskForm.value.requestType,
      priority: requestTaskForm.value.priority || 'normal',
      reason: requestTaskForm.value.reason || '',
      status: 'Pending',
      submittedDate: dayjs().format('YYYY-MM-DD')
    }
    
    // Add frequency data if it's a recurring task request
    if (requestTaskForm.value.requestType === 'recurring') {
      requestData.frequency = requestTaskForm.value.frequency
      requestData.frequencyNumber = requestTaskForm.value.frequencyNumber
      requestData.timeOfDay = requestTaskForm.value.timeOfDay?.format('HH:mm')
      requestData.dayOfWeek = requestTaskForm.value.dayOfWeek
      requestData.dayOfMonth = requestTaskForm.value.dayOfMonth
      requestData.month = requestTaskForm.value.month
      requestData.startDate = requestTaskForm.value.startDate?.format('YYYY-MM-DD')
      requestData.endDate = requestTaskForm.value.endDate?.format('YYYY-MM-DD')
    }
    
    // Call backend API to save request
    const response = await createTaskRequest(requestData)
    
    if (response?.data) {
      // Update local state for immediate UI update
      const newRequest = response.data
      changeRequests.value.unshift(newRequest)
      myRequests.value.unshift(newRequest)
      
      message.destroy()
      message.success('Task request submitted successfully!')
      requestTaskModalVisible.value = false
      
      // Reset form
      requestTaskForm.value = {
        requestType: '',
        title: '',
        description: '',
        priority: '',
        reason: '',
        frequency: '',
        frequencyNumber: 1,
        timeOfDay: null,
        dayOfWeek: '',
        dayOfMonth: null,
        month: '',
        startDate: null,
        endDate: null
      }
    }
  } catch (error) {
    message.destroy()
    const errorMsg = error?.response?.data?.msg || error?.message || 'Failed to submit request'
    message.error(errorMsg)
    console.error('Failed to submit request:', error)
  }
}

const showConfirmModal = (task, action) => {
  pendingTaskAction.value = { task, action }
  confirmReason.value = ''
  
  if (action === 'approve') {
    confirmModalTitle.value = 'Approve Task Completion'
    confirmModalMessage.value = `Are you sure you want to approve the completion of task "${task.title}"?`
  } else if (action === 'reject') {
    confirmModalTitle.value = 'Reject Task Completion'
    confirmModalMessage.value = `Are you sure you want to reject the completion of task "${task.title}"? The worker will need to redo the task.`
  }
  
  taskConfirmModalVisible.value = true
}

const handleTaskConfirmation = async () => {
  if (!pendingTaskAction.value) return
  
  const { task, action } = pendingTaskAction.value
  
  try {
    if (action === 'approve') {
      message.loading('Approving task completion...', 0)
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
      if (taskIndex !== -1) {
        todayTasks.value[taskIndex].status = 'Completed'
        if (confirmReason.value) {
          todayTasks.value[taskIndex].approvalReason = confirmReason.value
        }
      }
      
      message.destroy()
      message.success('Task completion approved!')
    } else if (action === 'reject') {
      message.loading('Rejecting task completion...', 0)
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
      if (taskIndex !== -1) {
        todayTasks.value[taskIndex].status = 'Rejected'
        if (confirmReason.value) {
          todayTasks.value[taskIndex].rejectionReason = confirmReason.value
        }
      }
      
      message.destroy()
      message.success('Task completion rejected! Worker will need to redo the task.')
    }
    
    taskConfirmModalVisible.value = false
    pendingTaskAction.value = null
  } catch (error) {
    message.destroy()
    message.error(`Failed to ${action} task completion`)
  }
}

// Request confirmation methods
const showRequestConfirmModal = (request, action) => {
  pendingRequestAction.value = { request, action }
  requestConfirmReason.value = ''
  
  if (action === 'approve') {
    requestConfirmModalTitle.value = 'Approve Request'
    requestConfirmModalMessage.value = `Are you sure you want to approve the request "${request.taskTitle}" from ${request.requester}?`
  } else if (action === 'reject') {
    requestConfirmModalTitle.value = 'Reject Request'
    requestConfirmModalMessage.value = `Are you sure you want to reject the request "${request.taskTitle}" from ${request.requester}?`
  }
  
  requestConfirmModalVisible.value = true
}

const viewRequestDetails = (request) => {
  selectedRequest.value = request
  requestDetailsModalVisible.value = true
}

const approveFromDetails = () => {
  requestDetailsModalVisible.value = false
  showRequestConfirmModal(selectedRequest.value, 'approve')
}

const rejectFromDetails = () => {
  requestDetailsModalVisible.value = false
  showRequestConfirmModal(selectedRequest.value, 'reject')
}

const handleRequestConfirmation = async () => {
  const { request, action } = pendingRequestAction.value
  
  try {
    message.loading(`${action === 'approve' ? 'Approving' : 'Rejecting'} request...`, 0)
    
    // Call backend API to approve or reject
    let response
    if (action === 'approve') {
      response = await approveTaskRequest(request.id, {
        approvalReason: requestConfirmReason.value || ''
      })
    } else {
      response = await rejectTaskRequest(request.id, {
        rejectionReason: requestConfirmReason.value || ''
      })
    }
    
    if (response?.data) {
      const updatedRequest = response.data
      
      // Update local state
      const requestIndex = changeRequests.value.findIndex(r => r.id === request.id)
      if (requestIndex !== -1) {
        changeRequests.value[requestIndex] = {
          ...changeRequests.value[requestIndex],
          status: updatedRequest.status,
          approvalReason: updatedRequest.approvalReason,
          rejectionReason: updatedRequest.rejectionReason
        }
      }
      
      // Also update POA's request status if it exists in myRequests
      const myRequestIndex = myRequests.value.findIndex(r => r.id === request.id)
      if (myRequestIndex !== -1) {
        myRequests.value[myRequestIndex] = {
          ...myRequests.value[myRequestIndex],
          status: updatedRequest.status,
          approvalReason: updatedRequest.approvalReason,
          rejectionReason: updatedRequest.rejectionReason
        }
      }
      
      // If approved, handle different request types
      if (action === 'approve') {
        // If it's a new task request, create the task
        if (request.requestType === 'new') {
          try {
            const newTask = {
              title: request.taskTitle,
              description: request.reason || request.description || '',
              priority: request.priority || 'normal',
              dueDate: request.startDate || dayjs().format('YYYY-MM-DD'),
              status: 'Pending',
              patientId: currentUser.value?.patientId || null,
              createdBy: currentUser.value?.id || null
            }
            const taskResponse = await createTask(newTask)
            if (taskResponse?.data) {
              // Task will be added when we reload the task list
              console.log('Task created from approved request:', taskResponse.data)
            }
          } catch (error) {
            console.error('Failed to create task from approved request:', error)
            // Don't fail the whole operation if task creation fails
          }
        }
        // If it's a recurring task request, create the recurring task template
        else if (request.requestType === 'recurring') {
          try {
            const newRecurringTask = {
              title: request.taskTitle,
              description: request.reason || '',
              assignedTo: null, // Manager can assign later
              frequency: request.frequency,
              frequencyNumber: request.frequencyNumber,
              timeOfDay: request.timeOfDay,
              dayOfWeek: request.dayOfWeek,
              dayOfMonth: request.dayOfMonth,
              month: request.month,
              startDate: request.startDate,
              endDate: request.endDate
            }
            const recurringResponse = await createRecurringTask(newRecurringTask)
            if (recurringResponse?.data) {
              recurringTasks.value.push(recurringResponse.data)
            }
          } catch (error) {
            console.error('Failed to create recurring task:', error)
            // Don't fail the whole operation if recurring task creation fails
          }
        }
      }
      
      // Reload tasks and pending requests after approval/rejection
      try {
        await loadTasks()
        // Reload pending requests for manager
        if (isManager.value) {
          try {
            const pendingRequestsResponse = await getPendingTaskRequests()
            if (pendingRequestsResponse && pendingRequestsResponse.data) {
              changeRequests.value = pendingRequestsResponse.data.map(req => ({
                id: req.id,
                requester: req.requester,
                taskTitle: req.taskTitle,
                requestType: req.requestType,
                frequency: req.frequency,
                frequencyNumber: req.frequencyNumber,
                reason: req.reason,
                status: req.status,
                approvalReason: req.approvalReason,
                rejectionReason: req.rejectionReason,
                timeOfDay: req.timeOfDay,
                dayOfWeek: req.dayOfWeek,
                dayOfMonth: req.dayOfMonth,
                month: req.month,
                startDate: req.startDate,
                endDate: req.endDate
              }))
            }
          } catch (error) {
            console.error('Failed to reload pending requests:', error)
          }
        }
      } catch (error) {
        console.error('Failed to reload tasks:', error)
      }
      
      message.destroy()
      message.success(`Request ${action === 'approve' ? 'approved' : 'rejected'} successfully!`)
      requestConfirmModalVisible.value = false
      pendingRequestAction.value = null
      requestConfirmReason.value = ''
    }
    
  } catch (error) {
    message.destroy()
    const errorMsg = error?.response?.data?.msg || error?.message || `Failed to ${action} request`
    message.error(errorMsg)
    console.error(`Failed to ${action} request:`, error)
  }
}

const workerCompleteTask = async (task) => {
  try {
    message.loading('Marking task as completed...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      todayTasks.value[taskIndex].status = 'Worker Completed'
    }
    
    message.destroy()
    message.success('Task marked as completed! Waiting for POA review.')
  } catch (error) {
    message.destroy()
    message.error('Failed to complete task')
  }
}

// Recurring task methods
const showCreateRecurringTaskModal = () => {
  createRecurringTaskForm.value = {
    title: '',
    description: '',
    assignedTo: '',
    frequency: '',
    frequencyNumber: 1,
    timeOfDay: null,
    dayOfWeek: '',
    dayOfMonth: null,
    month: '',
    startDate: dayjs(),
    endDate: null
  }
  createRecurringTaskModalVisible.value = true
}

const onFrequencyChange = () => {
  // Reset frequency-specific fields when frequency changes
  createRecurringTaskForm.value.dayOfWeek = ''
  createRecurringTaskForm.value.dayOfMonth = null
  createRecurringTaskForm.value.month = ''
  createRecurringTaskForm.value.timeOfDay = null
}

const onEditFrequencyChange = () => {
  // Reset frequency-specific fields when frequency changes in edit modal
  editRecurringTaskForm.value.dayOfWeek = ''
  editRecurringTaskForm.value.dayOfMonth = null
  editRecurringTaskForm.value.month = ''
  editRecurringTaskForm.value.timeOfDay = null
}

const confirmCreateRecurringTask = async () => {
  if (!createRecurringTaskForm.value.title || 
      !createRecurringTaskForm.value.frequency || !createRecurringTaskForm.value.startDate) {
    message.error('Please fill in all required fields')
    return
  }
  
  try {
    message.loading('Creating recurring task template...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newRecurringTask = {
      id: Date.now(),
      title: createRecurringTaskForm.value.title,
      description: createRecurringTaskForm.value.description,
      assignedTo: createRecurringTaskForm.value.assignedTo || null,
      frequency: createRecurringTaskForm.value.frequency,
      frequencyNumber: createRecurringTaskForm.value.frequencyNumber,
      timeOfDay: createRecurringTaskForm.value.timeOfDay?.format('HH:mm'),
      dayOfWeek: createRecurringTaskForm.value.dayOfWeek,
      dayOfMonth: createRecurringTaskForm.value.dayOfMonth,
      month: createRecurringTaskForm.value.month,
      startDate: createRecurringTaskForm.value.startDate.format('YYYY-MM-DD'),
      endDate: createRecurringTaskForm.value.endDate?.format('YYYY-MM-DD'),
      isActive: true,
      createdAt: dayjs().format('YYYY-MM-DD')
    }
    
    recurringTasks.value.push(newRecurringTask)
    
    // Generate tasks for the current date if applicable
    generateTasksFromTemplates(selectedDate.value)
    
    message.destroy()
    message.success('Recurring task template created successfully!')
    createRecurringTaskModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to create recurring task template')
  }
}

const editRecurringTask = (task) => {
  editRecurringTaskForm.value = {
    id: task.id,
    title: task.title,
    description: task.description,
    assignedTo: task.assignedTo,
    frequency: task.frequency,
    frequencyNumber: task.frequencyNumber || 1,
    timeOfDay: task.timeOfDay ? dayjs(task.timeOfDay, 'HH:mm') : null,
    dayOfWeek: task.dayOfWeek,
    dayOfMonth: task.dayOfMonth,
    month: task.month,
    startDate: dayjs(task.startDate),
    endDate: task.endDate ? dayjs(task.endDate) : null,
    isActive: task.isActive
  }
  editRecurringTaskModalVisible.value = true
}

const confirmEditRecurringTask = async () => {
  if (!editRecurringTaskForm.value.title || 
      !editRecurringTaskForm.value.frequency || !editRecurringTaskForm.value.startDate) {
    message.error('Please fill in all required fields')
    return
  }
  
  try {
    message.loading('Updating recurring task template...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = recurringTasks.value.findIndex(t => t.id === editRecurringTaskForm.value.id)
    if (taskIndex !== -1) {
      recurringTasks.value[taskIndex] = {
        ...recurringTasks.value[taskIndex],
        title: editRecurringTaskForm.value.title,
        description: editRecurringTaskForm.value.description,
        assignedTo: editRecurringTaskForm.value.assignedTo,
        frequency: editRecurringTaskForm.value.frequency,
        frequencyNumber: editRecurringTaskForm.value.frequencyNumber,
        timeOfDay: editRecurringTaskForm.value.timeOfDay?.format('HH:mm'),
        dayOfWeek: editRecurringTaskForm.value.dayOfWeek,
        dayOfMonth: editRecurringTaskForm.value.dayOfMonth,
        month: editRecurringTaskForm.value.month,
        startDate: editRecurringTaskForm.value.startDate.format('YYYY-MM-DD'),
        endDate: editRecurringTaskForm.value.endDate?.format('YYYY-MM-DD'),
        isActive: editRecurringTaskForm.value.isActive
      }
    }
    
    message.destroy()
    message.success('Recurring task template updated successfully!')
    editRecurringTaskModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to update recurring task template')
  }
}

const toggleRecurringTask = async (task) => {
  try {
    message.loading(`${task.isActive ? 'Deactivating' : 'Activating'} recurring task...`, 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = recurringTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      recurringTasks.value[taskIndex].isActive = !recurringTasks.value[taskIndex].isActive
    }
    
    message.destroy()
    message.success(`Recurring task ${task.isActive ? 'deactivated' : 'activated'} successfully!`)
  } catch (error) {
    message.destroy()
    message.error('Failed to update recurring task status')
  }
}

const handleDeleteRecurringTask = async (task) => {
  try {
    message.loading('Deleting recurring task template...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = recurringTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      recurringTasks.value.splice(taskIndex, 1)
    }
    
    message.destroy()
    message.success('Recurring task template deleted successfully!')
  } catch (error) {
    message.destroy()
    message.error('Failed to delete recurring task template')
  }
}

// Generate tasks from recurring templates for a specific date
const generateTasksFromTemplates = (date) => {
  const dateStr = date.format('YYYY-MM-DD')
  const dayOfWeek = date.format('dddd').toLowerCase()
  const dayOfMonth = date.date()
  const month = date.format('MMMM').toLowerCase()
  
  recurringTasks.value.forEach(template => {
    if (!template.isActive) return
    
    const startDate = dayjs(template.startDate)
    const endDate = template.endDate ? dayjs(template.endDate) : null
    const frequencyNumber = template.frequencyNumber || 1
    
    // Check if date is within range
    if (date.isBefore(startDate)) return
    if (endDate && date.isAfter(endDate)) return
    
    let shouldGenerate = false
    
    // Check if task should be generated for this date based on frequency and interval
    if (template.frequency === 'daily') {
      // For daily, check if the number of days since start date is divisible by frequencyNumber
      const daysSinceStart = date.diff(startDate, 'day')
      shouldGenerate = daysSinceStart % frequencyNumber === 0
    } else if (template.frequency === 'weekly' && template.dayOfWeek === dayOfWeek) {
      // For weekly, check if the number of weeks since start date is divisible by frequencyNumber
      const weeksSinceStart = date.diff(startDate, 'week')
      shouldGenerate = weeksSinceStart % frequencyNumber === 0
    } else if (template.frequency === 'monthly' && template.dayOfMonth === dayOfMonth) {
      // For monthly, check if the number of months since start date is divisible by frequencyNumber
      const monthsSinceStart = date.diff(startDate, 'month')
      shouldGenerate = monthsSinceStart % frequencyNumber === 0
    } else if (template.frequency === 'yearly' && template.month === month && template.dayOfMonth === dayOfMonth) {
      // For yearly, check if the number of years since start date is divisible by frequencyNumber
      const yearsSinceStart = date.diff(startDate, 'year')
      shouldGenerate = yearsSinceStart % frequencyNumber === 0
    }
    
    if (shouldGenerate) {
      // Check if task already exists for this date
      const existingTask = todayTasks.value.find(task => 
        task.title === template.title && 
        task.assignedTo === template.assignedTo &&
        task.dueDate === dateStr
      )
      
      if (!existingTask) {
        const newTask = {
          id: Date.now() + Math.random(),
          title: template.title,
          description: template.description,
          assignedTo: template.assignedTo || null,
          priority: 'normal', // Default priority for recurring tasks
          dueDate: dateStr,
          status: 'Pending',
          isRecurring: true,
          recurringTemplateId: template.id
        }
        
        todayTasks.value.push(newTask)
      }
    }
  })
}


// Helper functions
const getPriorityColor = (priority) => {
  switch (priority) {
    case 'normal': return 'green'
    case 'urgent': return 'orange'
    case 'very-urgent': return 'red'
    default: return 'default'
  }
}

const getPriorityText = (priority) => {
  switch (priority) {
    case 'normal': return 'Normal'
    case 'urgent': return 'Urgent'
    case 'very-urgent': return 'Very Urgent'
    default: return priority
  }
}

const getStatusColor = (status) => {
  switch (status) {
    case 'Pending': return 'orange'
    case 'In Progress': return 'blue'
    case 'Completed': return 'green'
    case 'Worker Completed': return 'gold'
    case 'Rejected': return 'red'
    case 'Cancelled': return 'red'
    case 'Assigned': return 'green'
    case 'Available': return 'blue'
    default: return 'default'
  }
}

const getRequestStatusColor = (status) => {
  switch (status) {
    case 'Pending': return 'orange'
    case 'Approved': return 'green'
    case 'Rejected': return 'red'
    default: return 'default'
  }
}

const getRequestTypeColor = (type) => {
  switch (type) {
    case 'new': return 'blue'
    case 'recurring': return 'purple'
    case 'modify': return 'orange'
    case 'remove': return 'red'
    case 'reschedule': return 'cyan'
    case 'Time Change': return 'orange'
    default: return 'default'
  }
}

const getRequestTypeText = (type) => {
  switch (type) {
    case 'new': return 'Add New Task'
    case 'recurring': return 'Add Recurring Task'
    case 'modify': return 'Edit Task'
    case 'remove': return 'Remove Task'
    case 'reschedule': return 'Reschedule Task'
    case 'Time Change': return 'Time Change'
    default: return type
  }
}

const getFrequencyColor = (frequency) => {
  switch (frequency) {
    case 'daily': return 'blue'
    case 'weekly': return 'green'
    case 'monthly': return 'orange'
    case 'yearly': return 'purple'
    default: return 'default'
  }
}

const getFrequencyText = (frequency, frequencyNumber = 1) => {
  const number = frequencyNumber || 1
  switch (frequency) {
    case 'daily': 
      return number === 1 ? 'Daily' : `Every ${number} Days`
    case 'weekly': 
      return number === 1 ? 'Weekly' : `Every ${number} Weeks`
    case 'monthly': 
      return number === 1 ? 'Monthly' : `Every ${number} Months`
    case 'yearly': 
      return number === 1 ? 'Yearly' : `Every ${number} Years`
    default: return frequency
  }
}

// Initialize
onMounted(async () => {
  selectedDate.value = dayjs()
  
  // Get current user information
  try {
    const response = await getMe()
    currentUser.value = response.data
    
    // Load tasks based on user role
    await loadTasks()
    
    // Update tasks for selected date
    updateTasksForDate(selectedDate.value)
  } catch (error) {
    console.error('Failed to get user info:', error)
    // Fallback to mock data if API fails
    updateTasksForDate(selectedDate.value)
  }
})

// Load tasks from API based on user role
const loadTasks = async () => {
  try {
    if (!currentUser.value) return
    
    let response
    if (currentUser.value.role === 'worker') {
      // Load tasks assigned to this worker
      response = await getTasksByWorker(currentUser.value.id)
    } else if (currentUser.value.role === 'poa') {
      // Load tasks for patient(s) this POA manages
      response = await getTasksByPatient(currentUser.value.patientId)
      
      // Load POA's own requests
      try {
        const myRequestsResponse = await getMyTaskRequests()
        if (myRequestsResponse && myRequestsResponse.data) {
          myRequests.value = myRequestsResponse.data.map(req => ({
            id: req.id,
            taskTitle: req.taskTitle,
            requestType: req.requestType,
            frequency: req.frequency,
            frequencyNumber: req.frequencyNumber,
            reason: req.reason,
            submittedDate: req.submittedDate,
            status: req.status,
            approvalReason: req.approvalReason,
            rejectionReason: req.rejectionReason
          }))
        }
      } catch (error) {
        console.error('Failed to load my requests:', error)
      }
    } else if (currentUser.value.role === 'manager') {
      // Load all tasks
      response = await getAllTasks()
      
      // Load available workers for task assignment
      try {
        console.log('🔍 Manager loading workers for task assignment - Manager ID:', currentUser.value.id)
        const workersResponse = await getWorkersByManagerId(currentUser.value.id)
        if (workersResponse && workersResponse.data) {
          availableWorkers.value = workersResponse.data.filter(worker => 
            worker.status === 'active' || worker.status === 'Active'
          )
          console.log('✅ Loaded available workers for task assignment:', availableWorkers.value.length)
          console.log('📋 Available workers:', availableWorkers.value.map(w => ({ id: w.id, name: w.name, workerId: w.workerId })))
        } else {
          console.warn('⚠️ No workers data returned for manager')
        }
      } catch (error) {
        console.error('❌ Failed to load workers for task assignment:', error)
        // Fallback to empty array if loading fails
        availableWorkers.value = []
      }
      
      // Load pending change requests for manager
      try {
        const pendingRequestsResponse = await getPendingTaskRequests()
        if (pendingRequestsResponse && pendingRequestsResponse.data) {
          changeRequests.value = pendingRequestsResponse.data.map(req => ({
            id: req.id,
            requester: req.requester,
            taskTitle: req.taskTitle,
            requestType: req.requestType,
            frequency: req.frequency,
            frequencyNumber: req.frequencyNumber,
            reason: req.reason,
            status: req.status,
            approvalReason: req.approvalReason,
            rejectionReason: req.rejectionReason,
            // Include recurring task fields
            timeOfDay: req.timeOfDay,
            dayOfWeek: req.dayOfWeek,
            dayOfMonth: req.dayOfMonth,
            month: req.month,
            startDate: req.startDate,
            endDate: req.endDate
          }))
        }
      } catch (error) {
        console.error('Failed to load pending requests:', error)
      }
    }
    
    if (response && response.data) {
      // Update tasks with real data
      todayTasks.value = response.data
    }
    
    // Load recurring tasks
    try {
      const recurringResponse = await getRecurringTasks()
      if (recurringResponse && recurringResponse.data) {
        recurringTasks.value = recurringResponse.data
      }
    } catch (error) {
      console.error('Failed to load recurring tasks:', error)
    }
    
  } catch (error) {
    console.error('Failed to load tasks:', error)
    // Show empty state if API fails
    todayTasks.value = []
    recurringTasks.value = []
  }
}
</script>

<style scoped>
.tasks-page {
  padding: 24px;
}

.ant-card {
  margin-bottom: 16px;
}
</style>
